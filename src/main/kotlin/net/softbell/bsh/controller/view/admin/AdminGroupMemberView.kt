package net.softbell.bsh.controller.view.admin

import net.softbell.bsh.domain.entity.Member
import net.softbell.bsh.dto.request.MemberGroupDto
import net.softbell.bsh.dto.request.MemberGroupPermissionDto
import net.softbell.bsh.dto.view.admin.group.MemberGroupInfoCardDto
import net.softbell.bsh.dto.view.admin.group.MemberGroupPermissionCardDto
import net.softbell.bsh.iot.service.v1.IotNodeServiceV1
import net.softbell.bsh.service.MemberService
import net.softbell.bsh.service.PermissionService
import net.softbell.bsh.service.ViewDtoConverterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

/**
 * @author : Bell(bell@softbell.net)
 * @description : 관리자 회원 그룹 관리 뷰 컨트롤러
 */
@Controller
@RequestMapping("/admin/group/member")
class AdminGroupMemberView {
    // Global Field
    private val G_BASE_REDIRECT_URL: String = "redirect:/admin/group/member"
    private val G_LOGOUT_REDIRECT_URL: String = "redirect:/logout"

    @Autowired private lateinit var viewDtoConverterService: ViewDtoConverterService
    @Autowired private lateinit var memberService: MemberService
    @Autowired private lateinit var permissionService: PermissionService
    @Autowired private lateinit var iotNodeService: IotNodeServiceV1

    @GetMapping
    fun dispGroupMember(model: Model): String {
        // Load
        model.addAttribute("listCardGroups", viewDtoConverterService.convMemberGroupSummaryCards(permissionService.getAllMemberGroup()))

        // Return
        return "services/admin/group/MemberGroup"
    }

    @GetMapping("/create")
    fun dispGroupCreate(model: Model): String {
        // Init
        model.addAttribute("listCardMembers", viewDtoConverterService.convGroupMemberCardItems(memberService.getAllMember()))

        // Return
        return "services/admin/group/MemberGroupCreate"
    }

    @GetMapping("/modify/{gid}")
    fun dispGroupModify(model: Model, @PathVariable("gid") gid: Long): String {
        // Init
        val listMember: MutableList<Member> = memberService.getAllMember() as MutableList<Member>
        val memberGroup = permissionService.getMemberGroup(gid)

        // Process
        for (entity in memberGroup!!.memberGroupItems)
            listMember.remove(entity.member)

        // View
        model.addAttribute("cardGroup", permissionService.getMemberGroup(gid)?.let { MemberGroupInfoCardDto(it) })
        model.addAttribute("listCardMembers", viewDtoConverterService.convGroupMemberCardItems(listMember))

        // Return
        return "services/admin/group/MemberGroupModify"
    }

    @GetMapping("/{gid}")
    fun dispGroup(model: Model, @PathVariable("gid") gid: Long): String {
        // Init
        model.addAttribute("cardPermission", MemberGroupPermissionCardDto(permissionService.getAllNodeGroup()))
        model.addAttribute("cardGroup", permissionService.getMemberGroup(gid)?.let { MemberGroupInfoCardDto(it) })

        // Return
        return "services/admin/group/MemberGroupInfo"
    }


    @PostMapping("/create")
    fun procGroupCreate(memberGroupDto: MemberGroupDto): String {
        // Init
        val isSuccess = permissionService.createMemberGroup(memberGroupDto)

        // Return
        return if (isSuccess)
            G_BASE_REDIRECT_URL
        else
            "$G_BASE_REDIRECT_URL?err"
    }

    @PostMapping("/modify/{gid}")
    fun procGroupModify(@PathVariable("gid") gid: Long, memberGroupDto: MemberGroupDto): String {
        // Init
        val isSuccess = permissionService.modifyMemberGroup(gid, memberGroupDto)

        // Return
        return if (isSuccess)
            "$G_BASE_REDIRECT_URL/$gid"
        else
            "$G_BASE_REDIRECT_URL/$gid?err"
    }

    @PostMapping("/enable")
    fun procGroupEnable(@RequestParam("gid") listGid: List<Long>): String {
        // Init
        val isSuccess = permissionService.enableMemberGroup(listGid)

        // Return
        return if (isSuccess)
            G_BASE_REDIRECT_URL
        else
            "$G_BASE_REDIRECT_URL?err"
    }

    @PostMapping("/disable")
    fun procGroupDisable(@RequestParam("gid") listGid: List<Long>): String {
        // Init
        val isSuccess = permissionService.disableMemberGroup(listGid)

        // Return
        return if (isSuccess)
            G_BASE_REDIRECT_URL
        else
            "$G_BASE_REDIRECT_URL?err"
    }

    @PostMapping("/delete")
    fun procGroupDelete(@RequestParam("gid") listGid: List<Long>): String {
        // Init
        val isSuccess = permissionService.deleteMemberGroup(listGid)

        // Return
        return if (isSuccess)
            G_BASE_REDIRECT_URL
        else
            "$G_BASE_REDIRECT_URL?err"
    }

    @PostMapping("/permission/add/{gid}")
    fun addPermission(@PathVariable("gid") gid: Long, memberGroupPermissionDto: MemberGroupPermissionDto): String {
        // Init
        val isSuccess = permissionService.addMemberPermission(gid, memberGroupPermissionDto)

        // Return
        return if (isSuccess)
            "$G_BASE_REDIRECT_URL/$gid"
        else
            "$G_BASE_REDIRECT_URL/$gid?err"
    }

    @PostMapping("/permission/delete/{gid}")
    fun deletePermission(@PathVariable("gid") gid: Long,
                         @RequestParam("pid") pid: Long): String {
        // Init
        val isSuccess = permissionService.deleteGroupPermission(pid)

        // Return
        return if (isSuccess)
            "$G_BASE_REDIRECT_URL/$gid"
        else
            "$G_BASE_REDIRECT_URL/$gid?err"
    }
}