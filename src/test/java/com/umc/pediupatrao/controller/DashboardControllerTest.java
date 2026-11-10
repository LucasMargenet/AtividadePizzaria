package com.umc.pediupatrao.controller;

import com.umc.pediupatrao.config.SecurityConfig;
import com.umc.pediupatrao.dto.IndicadoresGeraisDTO;
import com.umc.pediupatrao.service.DashboardService;
import com.umc.pediupatrao.service.UsuarioDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DashboardController.class)
@Import(SecurityConfig.class)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerenteConsultaIndicadoresComSucesso() throws Exception {
        when(dashboardService.obterIndicadores(any(), any())).thenReturn(new IndicadoresGeraisDTO());

        mockMvc.perform(get("/api/dashboard/indicadores"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ATENDENTE")
    void atendenteEBloqueadoDeConsultarIndicadores() throws Exception {
        mockMvc.perform(get("/api/dashboard/indicadores"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "GERENTE")
    void limiteInvalidoRetorna400() throws Exception {
        mockMvc.perform(get("/api/dashboard/produtos-mais-vendidos").param("limite", "0"))
                .andExpect(status().isBadRequest());
    }
}
