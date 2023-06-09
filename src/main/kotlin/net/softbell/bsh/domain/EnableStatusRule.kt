package net.softbell.bsh.domain

/**
 * @author : Bell(bell@softbell.net)
 * @description : 활성화 상태 자료형
 */
enum class EnableStatusRule {
    UNKNOWN { // 미확인
        override val value: String
            get() = "UNKNOWN"
        override val code: Int
            get() = -1
    },
    WAIT { // 인증 대기
        override val value: String
            get() = "WAIT"
        override val code: Int
            get() = 0
    },
    REJECT { // 접근 제한
        override val value: String
            get() = "REJECT"
        override val code: Int
            get() = 1
    },
    DISABLE { // 비활성화
        override val value: String
            get() = "DISABLE"
        override val code: Int
            get() = 2
    },
    ENABLE { // 활성화
        override val value: String
            get() = "ENABLE"
        override val code: Int
            get() = 3
    };

    abstract val value: String
    abstract val code: Int

    companion object {
        fun ofLegacyCode(legacyCode: Int): EnableStatusRule {
            for (rule in values())
                if (rule.code == legacyCode)
                    return rule
            return UNKNOWN
        }
    }
}