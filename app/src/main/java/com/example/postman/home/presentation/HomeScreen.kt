package com.example.postman.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.postman.R
import com.example.postman.core.KeyValueList
import com.example.postman.core.domain.model.ApiResponse
import com.example.postman.core.models.HttpMethod
import com.example.postman.core.presentation.icons.Add
import com.example.postman.core.presentation.icons.Arrow_drop_down
import com.example.postman.core.presentation.icons.Collections_bookmark
import com.example.postman.core.presentation.icons.Content_copy
import com.example.postman.core.presentation.icons.History
import com.example.postman.core.presentation.icons.Search
import com.example.postman.core.presentation.navigation.Screens
import com.example.postman.core.presentation.theme.DarkGreen
import com.example.postman.core.presentation.theme.Gray
import com.example.postman.core.presentation.theme.LightGray
import com.example.postman.core.presentation.theme.LightGreen
import com.example.postman.core.presentation.theme.Silver
import com.example.postman.core.presentation.theme.TextPrimary
import com.example.postman.home.domain.RadioHttpParameterOptions
import com.example.postman.home.presentation.components.KeyValueInput
import com.example.postman.home.presentation.components.RemovableTagList
import com.example.postman.home.presentation.components.SearchFromContentText
import com.example.postman.home.presentation.components.TextVisibilityTextField
import com.example.postman.home.presentation.util.getHeaderValue
import kotlinx.coroutines.launch


@Composable()
fun HomeScreen(
    homeViewModel: HomeViewModel,
    requestId: Int?,
    source: String?,
    collectionId: String?,
    onNavigateToHistory: () -> Unit,
    onNavigateToCollection: () -> Unit,
) {
    val uiState by homeViewModel.uiState.collectAsState()
    LaunchedEffect(requestId, source) {
        if (requestId != null && source != null) {
            when (source) {
                Screens.ROUTE_HISTORY_SCREEN -> homeViewModel.loadRequestFromHistory(requestId)
                Screens.ROUTE_COLLECTION_SCREEN -> homeViewModel.loadRequestFromCollection(requestId)
            }
        }
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current

    val callbacks = HomeCallbacks(
        onSendRequestClick = { homeViewModel.sendRequest(collectionId) },
        onBodyChanged = { homeViewModel.updateBody(it) },
        onAddHeader = { key, value -> homeViewModel.addHeader(key, value) },
        onRemoveHeader = { key, value -> homeViewModel.removeHeader(key, value) },
        onAddParameter = { key, value -> homeViewModel.addParameter(key, value) },
        onRemoveParameter = { key, value -> homeViewModel.removeParameter(key, value) },
        onHttpMethodChanged = { homeViewModel.updateHttpMethod(it) },
        onRequestUrlChanged = { homeViewModel.updateRequestUrl(it) },
        onClearDataClick = { homeViewModel.clearData() },
        onNavigateToHistory = onNavigateToHistory,
        onNavigateToCollection = onNavigateToCollection,
        onCopyClick = {
            val textToCopy =
                (uiState.response as? Loadable.Success)?.data?.response
                    ?: (uiState.response as? Loadable.Error)?.message
            textToCopy?.let {
                clipboard.setText(AnnotatedString(it))
                scope.launch {
                    snackbarHostState.showSnackbar("Copied to clipboard")
                }
            }
        }
    )
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        SnackbarHost(hostState = snackbarHostState, snackbar = { data ->
            Snackbar(
                containerColor = LightGreen,
                contentColor = Color.Black,
                snackbarData = data
            )
        })
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 24.dp)
            ) {
                BaseTextIconButton(
                    onClick = { callbacks.onNavigateToHistory() },
                    icon = History,
                    label = "History",
                )

                Spacer(modifier = Modifier.width(4.dp))
                BaseTextIconButton(
                    onClick = { callbacks.onNavigateToCollection() },
                    icon = Collections_bookmark,
                    label = "Collection"
                )
                Spacer(modifier = Modifier.width(4.dp))
                BaseTextIconButton(
                    onClick = { callbacks.onClearDataClick() },
                    icon = Add,
                    label = "Create new request"
                )
            }
            RequestBuilder(
                uiState,
                callbacks = callbacks
            )
        }
    }
}

@Composable
fun BaseTextIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
    tint: Color = TextPrimary,
) {
    TextButton(
        onClick = onClick,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint
            )
            Spacer(Modifier.width(4.dp))
            Text(label, color = tint)
        }
    }
}

@Composable
fun RequestBuilder(
    uiState: HomeUiState,
    callbacks: HomeCallbacks,
) {
    val httpMethods = listOf(
        HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH,
        HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS
    )
    val statusCode: Int? = (uiState.response as? Loadable.Success)?.data?.statusCode
    var isSearchVisible by remember { mutableStateOf(false) }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RequestLine(
            httpMethods,
            uiState.data.httpMethod,
            uiState.data.requestUrl,
            callbacks.onHttpMethodChanged,
            callbacks.onRequestUrlChanged,
            Modifier
                .weight(1f)
                .padding(start = 12.dp)
        )
        Button(
            modifier = Modifier
                .padding(4.dp),
            shape = RoundedCornerShape(4.dp),
            onClick = { callbacks.onSendRequestClick() }
        ) {
            Text(text = "Send", fontWeight = FontWeight.Bold)
        }
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(LightGreen)
            .padding(8.dp)
    ) {
        RequestParametersSection(
            modifier = Modifier,
            headers = uiState.data.headers,
            params = uiState.data.params,
            body = uiState.data.body,
            callbacks = callbacks
        )
    }


    Spacer(modifier = Modifier.height(8.dp))

    ResponseBodyTopBar(
        statusCode, isSearchVisible,
        onSearchClick = {
            isSearchVisible = it
        },
        callbacks = callbacks
    )

    HorizontalDivider(
        thickness = 0.5.dp,
        modifier = Modifier.padding(4.dp),
        color = MaterialTheme.colorScheme.primary
    )

    ResponseBody(
        response = uiState.response,
        isSearchVisible = isSearchVisible,
        onDismissSearch = { isSearchVisible = false }
    )
}

@Composable
private fun RequestLine(
    httpMethods: List<HttpMethod>,
    selectedHttpMethod: HttpMethod,
    requestUrl: String,
    onHttpMethodChanged: (HttpMethod) -> Unit,
    onRequestUrlChanged: (String) -> Unit,
    modifier: Modifier,
) {
    var isHttpMethodExpanded: Boolean by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .border(
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(0.5.dp, color = MaterialTheme.colorScheme.primary)
            ),
    ) {
        Text(
            selectedHttpMethod.name,
            color = selectedHttpMethod.color,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(12.dp)
                .clickable { isHttpMethodExpanded = true })
        Icon(
            imageVector = Arrow_drop_down,
            modifier = Modifier
                .clickable { isHttpMethodExpanded = true },
            contentDescription = "drop down icon"
        )

        DropdownMenu(
            expanded = isHttpMethodExpanded,
            onDismissRequest = { isHttpMethodExpanded = false }
        ) {
            httpMethods.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name, color = option.color) },
                    onClick = {
                        isHttpMethodExpanded = false
                        onHttpMethodChanged(option)
                    })
            }
        }

        TextField(
            value = requestUrl,
            onValueChange = {
                onRequestUrlChanged(it)
            },
            maxLines = 3,
            placeholder = { Text("Enter URL", color = Silver) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
    }
}

@Composable
fun RequestParametersSection(
    modifier: Modifier,
    headers: KeyValueList?,
    params: KeyValueList?,
    body: String?,
    callbacks: HomeCallbacks,
) {
    val radioHttpParameterOptions = RadioHttpParameterOptions.entries.toList()
    var (selectedOption, onOptionSelected) = remember { mutableStateOf(radioHttpParameterOptions[0]) }

    Column(modifier = modifier.fillMaxWidth()) {
        HttpParameterSelection(radioHttpParameterOptions, selectedOption, onOptionSelected)
        HttpParameterBody(
            modifier,
            selectedOption,
            headers,
            params,
            body,
            callbacks
        )
    }
}

@Composable
private fun HttpParameterSelection(
    radioHttpParameterOptions: List<RadioHttpParameterOptions>,
    selectedOption: RadioHttpParameterOptions,
    onOptionSelected: (RadioHttpParameterOptions) -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .selectableGroup()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        radioHttpParameterOptions.forEach { option ->
            Row(
                modifier = Modifier
                    .selectable(
                        selected = (option == selectedOption),
                        onClick = {
                            onOptionSelected(option)
                        }, role = Role.RadioButton
                    )
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (option == selectedOption), onClick = null,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = MaterialTheme.colorScheme.primary,
                        unselectedColor = MaterialTheme.colorScheme.primary
                    ), modifier = Modifier.padding(end = 2.dp)
                )
                Text(text = option.name)
            }
        }
    }
}

@Composable
private fun HttpParameterBody(
    modifier: Modifier,
    selectedOption: RadioHttpParameterOptions,
    headers: KeyValueList?,
    params: KeyValueList?,
    body: String?,
    callbacks: HomeCallbacks,
) {
    Box(
        modifier
            .height(120.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        when (selectedOption) {
            RadioHttpParameterOptions.Auth -> AuthSection(
                Modifier.padding(12.dp), headers, callbacks
            )

            RadioHttpParameterOptions.Params -> ParamsSection(
                params, callbacks
            )

            RadioHttpParameterOptions.Header -> HeaderSection(
                headers, callbacks
            )

            RadioHttpParameterOptions.Body -> HttpParameterBodySection(
                Modifier.fillMaxSize(), body, callbacks
            )
        }
    }
}

@Composable
private fun StatusCode(statusCode: Int?) {
    if (statusCode == null) return
    val textColor = if (statusCode in 200..208) DarkGreen else MaterialTheme.colorScheme.error
    val backgroundColor =
        if (statusCode in 200..208) LightGreen else MaterialTheme.colorScheme.errorContainer

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Status ", modifier = Modifier
                .padding(start = 24.dp), color = Gray, fontSize = 12.sp
        )
        Text(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(
                    backgroundColor
                )
                .padding(horizontal = 4.dp),
            color = textColor,
            text = statusCode.toString(),
        )
    }
}

@Composable
fun ParamsSection(
    params: KeyValueList?,
    callbacks: HomeCallbacks,
) {
    Column {
        KeyValueInput { key, value ->
            callbacks.onAddParameter(key, value)
        }
        Spacer(modifier = Modifier.height(4.dp))
        RemovableTagList(
            items = params,
            onRemoveItem = { key, value ->
                callbacks.onRemoveParameter(key, value)
            }
        )
    }
}

@Composable
fun AuthSection(
    modifier: Modifier,
    headers: KeyValueList?,
    callbacks: HomeCallbacks,
) {
    Column(modifier) {
        Text(
            text = "Bearer Token", modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(4.dp)
        )
        Spacer(Modifier.height(8.dp))
        TextVisibilityTextField(
            headers?.getHeaderValue("Authorization") ?: "",
            onTextChange = {
                callbacks.onAddHeader("Authorization", it)
            })
    }
}


@Composable
fun HeaderSection(
    headers: KeyValueList?,
    callbacks: HomeCallbacks,
) {
    Column {
        KeyValueInput { key, value ->
            callbacks.onAddHeader(key, value)
        }
        Spacer(modifier = Modifier.height(4.dp))
        RemovableTagList(
            items = headers,
            onRemoveItem = { key, value ->
                callbacks.onRemoveHeader(key, value)
            }
        )
    }
}


@Composable
fun HttpParameterBodySection(
    modifier: Modifier,
    body: String?,
    callbacks: HomeCallbacks,
) {
    TextField(
        value = body ?: "",
        onValueChange = {
            callbacks.onBodyChanged(it)
        },
        maxLines = Int.MAX_VALUE,
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(8.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
private fun ResponseBodyTopBar(
    statusCode: Int?,
    searchVisible: Boolean,
    onSearchClick: (Boolean) -> Unit,
    callbacks: HomeCallbacks,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "{ } JSON",
            fontSize = 14.sp,
            modifier = Modifier
                .background(LightGray, shape = RoundedCornerShape(8.dp))
                .padding(6.dp, 2.dp)
        )

        StatusCode(statusCode)

        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = { onSearchClick(!searchVisible) },
            modifier = Modifier
                .padding(end = 8.dp)
                .size(24.dp)
        ) {
            Icon(
                imageVector = Search,
                contentDescription = "search"
            )
        }
        IconButton(
            onClick = {
                callbacks.onCopyClick()
            },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Content_copy,
                contentDescription = "copy response"
            )
        }
    }
}

@Composable
fun ResponseBody(
    response: Loadable<ApiResponse>,
    isSearchVisible: Boolean,
    onDismissSearch: () -> Unit,
) {
    when (response) {
        is Loadable.Success -> {
            SearchFromContentText(
                response.data.response,
                isSearchVisible,
                onDismissSearch = onDismissSearch
            )
            if (response.data.imageResponse != null) {
                Image(
                    bitmap = response.data.imageResponse,
                    contentDescription = "Decoded Image",
                    modifier = Modifier.fillMaxWidth(), alignment = Alignment.Center,
                )
            }
        }

        is Loadable.Error -> Text(
            text = "Error: ${response.message}",
            modifier = Modifier.padding(16.dp), color = Color.Red
        )

        Loadable.Loading -> CircularProgressIndicator()
        is Loadable.NetworkError -> Text(
            text = "Error: ${response.message}",
            modifier = Modifier.padding(16.dp), color = Color.Red
        )

        is Loadable.Empty -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    painter = painterResource(R.drawable.action_block),
                    contentDescription = "no responses yet",
                    tint = Silver
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter the URL and click send to get a response",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}