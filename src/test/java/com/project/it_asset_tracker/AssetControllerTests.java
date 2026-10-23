package com.project.it_asset_tracker;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AssetControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedUserShouldNotAccessAssets() throws Exception {
        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "staff_test", roles = "STAFF")
    void staffShouldAccessAssetList() throws Exception {
        mockMvc.perform(get("/api/assets"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "staff_test", roles = "STAFF")
    void staffShouldNotAssignAssets() throws Exception {
        mockMvc.perform(patch("/api/assets/1/assign/3"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "staff_test", roles = "STAFF")
    void staffShouldNotReturnAssets() throws Exception {
        mockMvc.perform(patch("/api/assets/1/return"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin_test", roles = "ADMIN")
    void adminRequestForMissingAssetShouldReturnNotFound()
            throws Exception {
        mockMvc.perform(get("/api/assets/999999"))
                .andExpect(status().isNotFound());
    }
}