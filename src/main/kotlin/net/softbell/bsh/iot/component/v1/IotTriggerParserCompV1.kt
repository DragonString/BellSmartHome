package net.softbell.bsh.iot.component.v1

import mu.KLogging
import net.softbell.bsh.domain.EnableStatusRule
import net.softbell.bsh.domain.TriggerLastStatusRule
import net.softbell.bsh.domain.TriggerStatusRule
import net.softbell.bsh.domain.entity.NodeAction
import net.softbell.bsh.domain.entity.NodeItem
import net.softbell.bsh.domain.entity.NodeItemHistory
import net.softbell.bsh.domain.entity.NodeTrigger
import net.softbell.bsh.domain.repository.NodeItemHistoryRepo
import net.softbell.bsh.domain.repository.NodeItemRepo
import net.softbell.bsh.domain.repository.NodeTriggerRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import java.util.*
import kotlin.math.abs

/**
 * @author : Bell(bell@softbell.net)
 * @description : IoT 트리거 파서 컴포넌트 v1
 * 표현식: {아이템ID.내장함수(매개변수)}==값 and ~~
 * [1.last(0)==0 and 2.last(0)!=0] or 3.last(0)>0
 */
@Component
class IotTriggerParserCompV1 {
    // Global Field
    @Autowired private lateinit var nodeTriggerRepo: NodeTriggerRepo
    @Autowired private lateinit var nodeItemRepo: NodeItemRepo
    @Autowired private lateinit var nodeItemHistoryRepo: NodeItemHistoryRepo


    internal enum class BuiltInFunction {
        UNKNOWN { // 미확인
            override val value: String
                get() = "unknown"
        },
        LAST { // 마지막 값 조회
            override val value: String
                get() = "last"
        },
        MAX { // 최대 값 조회
            override val value: String
                get() = "max"
        },
        MIN { // 최소 값 조회
            override val value: String
                get() = "min"
        },
        AVG { // 평균 값 조회
            override val value: String
                get() = "avg"
        },
        DIFF { // last(0)과 last(1)의 차이 여부(1 or 0)
            override val value: String
                get() = "diff"
        },
        CHANGE {// last(0) - last(1) 의 값
            override val value: String
                get() = "change"
        },
        ABSCHANGE { // change의 절대값
            override val value: String
            get() = "abschange"
        },
        DELTA { // 최근 n 초 간 max - min 값
            override val value: String
            get() = "delta"
        };

        abstract val value: String

        companion object {
            fun ofValue(value: String): BuiltInFunction {
                for (funcType in values())
                    if (funcType.value == value)
                        return funcType
                return UNKNOWN
            }
        }
    }

    internal enum class RelationalOperatorType {
        UNKNOWN { // 미확인
            override val value: String
                get() = "unknown"
        },
        EQ { // = : Equal
            override val value: String
                get() = "=="
        },
        NE { // != : Not Equal
            override val value: String
                get() = "!="
        },
        GE { // >= : Greater Equal
            override val value: String
                get() = ">="
        },
        GT { // > : Greater Then
            override val value: String
                get() = ">"
        },
        LE { // <= : Little Equal
            override val value: String
                get() = "<="
        },
        LT { // < : Little Then
            override val value: String
                get() = "<"
        };

        abstract val value: String

        companion object {
            fun ofValue(value: String): RelationalOperatorType {
                for (opType in values())
                    if (opType.value == value)
                        return opType
                return UNKNOWN
            }
        }
    }

    internal enum class LogicalOperatorType {
        UNKNOWN { // 미확인
            override val value: String
                get() = "unknown"
        },
        AND { // &&
            override val value: String
                get() = "and"
        },
        OR { // ||
            override val value: String
                get() = "or"
        };

        abstract val value: String

        companion object {
            fun ofValue(value: String): LogicalOperatorType {
                for (opType in values())
                    if (opType.value == value)
                        return opType
                return UNKNOWN
            }
        }
    }


    fun getTriggerAction(nodeTrigger: NodeTrigger): List<NodeAction> {
        // Init
        val listNodeAction: MutableList<NodeAction> = ArrayList()
        logger.info("트리거 액션 로드 (" + nodeTrigger.lastStatus + ") - " + nodeTrigger.description)

        // Process
        for (entity in nodeTrigger.nodeTriggerActions)
            when (nodeTrigger.lastStatus) {
            TriggerLastStatusRule.OCCUR -> if (entity.triggerStatus != TriggerStatusRule.ERROR && entity.triggerStatus != TriggerStatusRule.RESTORE)
                listNodeAction.add(entity.nodeAction) // Trigger 발생 액션
            TriggerLastStatusRule.RESTORE -> if (entity.triggerStatus != TriggerStatusRule.ERROR && entity.triggerStatus != TriggerStatusRule.OCCUR)
                listNodeAction.add(entity.nodeAction) // Trigger 복구 액션
            TriggerLastStatusRule.UNKNOWN -> if (entity.triggerStatus == TriggerStatusRule.ALL || entity.triggerStatus == TriggerStatusRule.ERROR)
                listNodeAction.add(entity.nodeAction) // Trigger 미확인 액션
            else -> {
            }
        }

        // Return
        return listNodeAction
    }

    fun convTrigger(nodeItem: NodeItem): List<NodeTrigger> {
        // Field
        val listNodeTrigger: List<NodeTrigger>
        var expression: String?

        // Init
        expression = "{"
        expression += nodeItem.itemId
        expression += "."

        // Process
        listNodeTrigger = nodeTriggerRepo.findByEnableStatusAndExpressionContaining(EnableStatusRule.ENABLE, expression)

        // Return
        return listNodeTrigger
    }

    fun parseEntity(nodeTrigger: NodeTrigger): Boolean? {
        // Init
        var intParentCount = 10 // 괄호 최대 10개로 제한
        val intLoopCount = 10 // 괄호 내 아이템 최대 10개로 제한
        val rawExpression = nodeTrigger.expression
        var resultExpression = rawExpression
        var parseExpression: String?

//		logger.info("표현식 분석 시작: " + rawExpression);

        // Process - []
        while (intParentCount-- > 0) {
            // Init
            val idxParentStart = resultExpression.indexOf("[")
            val idxParentEnd = resultExpression.indexOf("]")
            parseExpression = if (idxParentStart == -1)
                resultExpression
            else
                resultExpression.substring(idxParentStart + 1, idxParentEnd)

            // Process - Item
            parseExpression = procItem(parseExpression, intLoopCount) ?: return null

            // Post Process
            parseExpression = parseLogical(parseExpression) // 논리 연산자 계산

            // Replace
            if (idxParentStart == -1 && parseExpression != null)
                resultExpression = parseExpression
            else
                if (parseExpression != null)
                    resultExpression = resultExpression.replace(resultExpression.substring(idxParentStart, idxParentEnd + 1), parseExpression)

            // Finish Check
            if (resultExpression.length <= 1)
                break
        }

		logger.info("표현식 분석 끝: $resultExpression");

        // Return
        if (resultExpression == "1") // 트리거 활성화
            return true
        else if (resultExpression == "0") // 트리거 복구
            return false
        return null // 파싱 에러
    }

    private fun procItem(parseExpression: String, intLoopCount: Int): String? {
        var parseExpression = parseExpression
        var intLoopCount = intLoopCount
        while (intLoopCount-- > 0) {
            // Field
            var idxItemStart: Int
            var idxItemIdEnd: Int
            var idxParamStart: Int
            var idxParamEnd: Int
            var idxItemEnd: Int
            var idxOperator: Int
            var idxCompOperator: Int
            var itemId: Long
            var funcType: BuiltInFunction?
            var relOpType: RelationalOperatorType?
            var strFunc: String
            var strParam: String
            var strItemId: String
            var strRelValue: String
            var strResult: String
            var itemStatus: Double?
            var relValue: Double
            var isResult: Boolean?

            // Init
            idxOperator = -1
            idxCompOperator = -1
            relOpType = null

            // Parse
            idxItemStart = parseExpression.indexOf("{") // 표현식 값 치환 시작부분 탐색
            idxItemIdEnd = parseExpression.indexOf(".", idxItemStart) // 표현식 내장함수 시작부분 탐색
            idxParamStart = parseExpression.indexOf("(", idxItemIdEnd) // 표현식 내장함수 매개변수 시작부분 탐색
            idxParamEnd = parseExpression.indexOf(")", idxParamStart) // 표현식 내장함수 매개변수 끝부분 탐색
            idxItemEnd = parseExpression.indexOf("}", idxParamEnd) // 표현식 값 치환 닫는부분 탐색
            for (opType in RelationalOperatorType.values()) {
                val idxTemp = parseExpression.indexOf(opType.value, idxItemEnd) // 표현식 관계 연산자 탐색

                if (idxTemp != -1 && (idxOperator == -1 || idxOperator > idxTemp)) {
                    idxOperator = idxTemp
                    relOpType = opType
                }
            }
            for (opType in LogicalOperatorType.values()) {
                val idxTemp = parseExpression.indexOf(opType.value, idxOperator) // 표현식 논리 연산자 탐색

                if (idxTemp != -1 && (idxCompOperator == -1 || idxCompOperator > idxTemp))
                    idxCompOperator = idxTemp
            }

            // Exception
            if (idxItemStart == -1)
                break
            else if (idxItemStart == -1 || idxItemIdEnd == -1 || idxParamStart == -1 || idxParamEnd == -1)
                return null
            if (relOpType == null)
                return null

            // Process
            strItemId = parseExpression.substring(idxItemStart + 1, idxItemIdEnd)
            strFunc = parseExpression.substring(idxItemIdEnd + 1, idxParamStart)
            strParam = parseExpression.substring(idxParamStart + 1, idxParamEnd)
            strRelValue = if (idxCompOperator == -1)
                parseExpression.substring(idxOperator + relOpType.value.length)
            else
                parseExpression.substring(idxOperator + relOpType.value.length, idxCompOperator)
            try {
                itemId = java.lang.Long.valueOf(strItemId)
                relValue = java.lang.Double.valueOf(strRelValue)
                funcType = BuiltInFunction.ofValue(strFunc)
            } catch (ex: Exception) {
                logger.error("무슨 에러? " + ex.message)
                return null
            }

            // Load
            itemStatus = getItemStatus(itemId, funcType, strParam)

            // Relational Process
            isResult = procRelational(relOpType, itemStatus!!, relValue) // 관계 연산자 분석

            // Post Process
            strResult = if (isResult == null)
                return null
            else if (isResult)
                "1"
            else
                "0"

            // Replace
            parseExpression = if (idxCompOperator == -1)
                parseExpression.replace(parseExpression.substring(idxItemStart), strResult)
            else
                parseExpression.replace(parseExpression.substring(idxItemStart, idxCompOperator - 1), strResult)
        }

        // Return
        return parseExpression
    }

    private fun getItemStatus(itemId: Long, funcType: BuiltInFunction, param: String): Double? {
        // Field
        val nodeItem: NodeItem
        var result: Double? = null
        var dateStart: Date? = null
        var curPage: Pageable? = null
        var intParam = 0

        // Init
        val optNodeItem = nodeItemRepo.findById(itemId)

        // Exception
        if (!optNodeItem.isPresent)
            return null

        // Load
        nodeItem = optNodeItem.get()

        // Process - Param Parse
        // No Parameter Function
        intParam = if (funcType == BuiltInFunction.DIFF || funcType == BuiltInFunction.CHANGE || funcType == BuiltInFunction.ABSCHANGE)
            0 // Set Parameter
        else {
            try {
                Integer.valueOf(param)
            } catch (ex: Exception) {
                logger.error("표현식 매개변수 정수 변환 에러: $param")
                return null
            }
        }

        // Process - DB 1 Record Find
        if (funcType == BuiltInFunction.LAST || funcType == BuiltInFunction.DIFF || funcType == BuiltInFunction.CHANGE || funcType == BuiltInFunction.ABSCHANGE) {
            // Field
            val listNodeItemHistory: List<NodeItemHistory>

            // Init
            curPage = if (funcType == BuiltInFunction.LAST) PageRequest.of(intParam, 1) // Page Set
            else PageRequest.of(0, 2) // Page Set

            // Load
            val pageNodeItemHistory = nodeItemHistoryRepo.findByNodeItemOrderByItemHistoryIdDesc(nodeItem, curPage)

            // Exception
            if (pageNodeItemHistory.isEmpty)
                return null

            // Post Load
            listNodeItemHistory = pageNodeItemHistory.content

            // Process - Last
            if (funcType == BuiltInFunction.LAST)
                result = listNodeItemHistory[0].itemStatus
            else {
                // Process - Other
                // Exception
                if (listNodeItemHistory.size < 2)
                    return null

                // Init
                val lastStatus = listNodeItemHistory[0].itemStatus
                val beforeStatus = listNodeItemHistory[1].itemStatus

                when (funcType) {
                    BuiltInFunction.DIFF -> result = if (lastStatus != beforeStatus) 1.0 else 0.0 // TODO
                    BuiltInFunction.CHANGE -> result = lastStatus - beforeStatus
                    BuiltInFunction.ABSCHANGE -> result = abs(lastStatus - beforeStatus)
                    else -> {
                    }
                }
            }
        } else if (funcType == BuiltInFunction.AVG || funcType == BuiltInFunction.MIN || funcType == BuiltInFunction.MAX || funcType == BuiltInFunction.DELTA) {
            // Field
            val calendar = Calendar.getInstance()

            // Init
            calendar.add(Calendar.SECOND, intParam)
            dateStart = calendar.time

            when (funcType) {
                BuiltInFunction.AVG -> result = nodeItemHistoryRepo.avgByNodeItem(nodeItem, dateStart)
                BuiltInFunction.MIN -> result = nodeItemHistoryRepo.minByNodeItem(nodeItem, dateStart)
                BuiltInFunction.MAX -> result = nodeItemHistoryRepo.maxByNodeItem(nodeItem, dateStart)
                BuiltInFunction.DELTA -> {
                    // Init
                    val itemMax = nodeItemHistoryRepo.maxByNodeItem(nodeItem, dateStart)!!
                    val itemMin = nodeItemHistoryRepo.minByNodeItem(nodeItem, dateStart)!!

                    // Process
                    result = itemMax - itemMin
                }
                else -> {
                }
            }
        } else
            return null // Not Defined

        // Return
        return result
    }

    private fun procRelational(relOpType: RelationalOperatorType, itemStatus: Double, relValue: Double): Boolean? {
        return when (relOpType) {
            RelationalOperatorType.EQ -> itemStatus == relValue
            RelationalOperatorType.NE -> itemStatus != relValue
            RelationalOperatorType.GT -> itemStatus > relValue
            RelationalOperatorType.GE -> itemStatus >= relValue
            RelationalOperatorType.LT -> itemStatus < relValue
            RelationalOperatorType.LE -> itemStatus <= relValue
            else -> return null
        }
    }

    private fun parseLogical(parseExpression: String): String? {
        var parseExpression = parseExpression
        for (opType in LogicalOperatorType.values()) {
            // Field
            var intLogicalCount = 10

            // Logical Process
            while (intLogicalCount-- > 0) {
                // Init
                var val1 = false
                var val2 = false
                val idxTemp = parseExpression.indexOf(opType.value) // 표현식 논리 연산자 탐색

                // Exception
                if (idxTemp == -1)
                    break

                // Load
                val strVal1 = parseExpression.substring(idxTemp - 2, idxTemp - 1)
                val strVal2 = parseExpression.substring(idxTemp + opType.value.length + 1, idxTemp + opType.value.length + 2)
                try {
                    if (strVal1 == "1")
                        val1 = true
                    if (strVal2 == "1")
                        val2 = true
                } catch (ex: Exception) {
                    logger.error("파싱 에러: " + ex.message)
                    return null
                }
                val isResult = when (opType) {
                    LogicalOperatorType.AND -> val1 && val2
                    LogicalOperatorType.OR -> val1 || val2
                    else -> {
                        logger.error("여기가 와지나?")
                        return null
                    }
                }
                val strResult = if (isResult) "1" else "0"

                // Replace
                parseExpression = parseExpression.replace(parseExpression.substring(idxTemp - 2, idxTemp + opType.value.length + 2), strResult)
            }
        }

        // Return
        return parseExpression
    }

    companion object : KLogging()
}