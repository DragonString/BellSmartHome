package net.softbell.bsh.filter

import net.softbell.bsh.service.CenterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import java.io.IOException
import javax.servlet.*

/**
 * @author : Bell(bell@softbell.net)
 * @description : 센터 설정 오버라이드 필터
 */
@Component
@Order(1)
class CenterFilter : Filter {
    @Autowired private lateinit var centerService: CenterService

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {
    }

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
        // Anything..
        servletRequest.setAttribute("setting", centerService.setting)

        // Next Filter
        filterChain.doFilter(servletRequest, servletResponse)
    }

    override fun destroy() {}
}