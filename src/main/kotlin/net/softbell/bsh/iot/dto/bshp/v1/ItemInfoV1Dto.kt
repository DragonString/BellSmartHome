package net.softbell.bsh.iot.dto.bshp.v1

/**
 * @author : Bell(bell@softbell.net)
 * @description : BSHPv1 전용 Items Info DTO
 */
data class ItemInfoV1Dto (
    var controlMode: Byte,
    var itemIndex: Byte,
    var itemMode: Int,
    var itemCategory: Int,
    var itemType: Int,
    var itemName: String
)