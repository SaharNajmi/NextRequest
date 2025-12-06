package com.example.nextrequest.home.presentation

import com.example.nextrequest.core.domain.model.ApiRequest
import com.example.nextrequest.core.domain.model.ApiResponse

data class HomeUiState(
    val data: ApiRequest,
    val response: Loadable<ApiResponse>,
)