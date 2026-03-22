package com.example.nextrequest.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nextrequest.core.domain.model.ApiResponse
import com.example.nextrequest.core.models.HttpMethod
import com.example.nextrequest.core.models.KeyValue
import com.example.nextrequest.core.presentation.color
import com.example.nextrequest.core.presentation.component.AppSnackbar
import com.example.nextrequest.core.presentation.icons.Add
import com.example.nextrequest.core.presentation.icons.Collections_bookmark
import com.example.nextrequest.core.presentation.icons.Content_copy
import com.example.nextrequest.core.presentation.icons.History
import com.example.nextrequest.core.presentation.icons.Search
import com.example.nextrequest.core.presentation.icons.Send
import com.example.nextrequest.core.presentation.icons.TriangleDown
import com.example.nextrequest.core.presentation.theme.cardBackground
import com.example.nextrequest.core.presentation.theme.cardBorder
import com.example.nextrequest.core.presentation.theme.chipTintAlpha
import com.example.nextrequest.core.presentation.theme.dropdownBorder
import com.example.nextrequest.core.presentation.theme.iconOnBackground
import com.example.nextrequest.core.presentation.theme.inputFieldColors
import com.example.nextrequest.core.presentation.theme.jsonChipBackground
import com.example.nextrequest.core.presentation.theme.jsonChipBorder
import com.example.nextrequest.core.presentation.theme.textMuted
import com.example.nextrequest.home.domain.RadioHttpParameterOptions
import com.example.nextrequest.home.presentation.components.KeyValueInput
import com.example.nextrequest.home.presentation.components.RemovableTagList
import com.example.nextrequest.home.presentation.components.SearchFromContentText
import com.example.nextrequest.home.presentation.components.TextVisibilityTextField
import com.example.nextrequest.home.presentation.util.getHeaderValue
import com.sahar.nextrequest.R
import kotlinx.coroutines.launch

private val HTTP_METHODS = listOf(
    HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH,
    HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS
)

private val HTTP_PARAM_OPTIONS = RadioHttpParameterOptions.entries.toList()

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    requestId: Int?,
    source: String?,
    collectionId: String?,
    onNavigateToHistory: () -> Unit,
    onNavigateToCollection: () -> Unit,
    onNavigateToWebSocket: () -> Unit,
) {
    val uiState by homeViewModel.uiState.collectAsState()
    LaunchedEffect(requestId, source) {
        if (requestId != null && source != null) {
            homeViewModel.loadRequest(requestId, source)
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
        onNavigateToWebSocket = onNavigateToWebSocket,
        onCopyClick = {
            val textToCopy = (uiState.response as? Loadable.Success)?.data?.response
                ?: (uiState.response as? Loadable.Error)?.message
            textToCopy?.let {
                clipboard.setText(AnnotatedString(it))
                scope.launch { snackbarHostState.showSnackbar("Copied to clipboard") }
            }
        }
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
        ) {
            AppTopBar(callbacks = callbacks)
            RequestBuilder(
                uiState = uiState,
                callbacks = callbacks,
                modifier = Modifier.weight(1f)
            )
        }
        SnackbarHost(hostState = snackbarHostState) { data ->
            AppSnackbar(message = data.visuals.message)
        }
    }
}

@Composable
private fun AppTopBar(callbacks: HomeCallbacks) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "NextRequest",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            modifier = Modifier.weight(1f)
        )
        TopBarIcon(History, "History", callbacks.onNavigateToHistory)
        TopBarIcon(Collections_bookmark, "Collection", callbacks.onNavigateToCollection)
        TopBarIcon(Add, "New request", callbacks.onClearDataClick)
        WebSocketChip(onClick = callbacks.onNavigateToWebSocket)
    }
}

@Composable
private fun TopBarIcon(icon: ImageVector, contentDescription: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(36.dp)) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.textMuted,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun WebSocketChip(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
        color = MaterialTheme.colorScheme.primary.copy(alpha = MaterialTheme.colorScheme.chipTintAlpha),
        modifier = Modifier.padding(start = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.websocket),
                contentDescription = "WebSocket",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(12.dp)
            )
            Text(
                "WS",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                letterSpacing = 0.3.sp
            )
        }
    }
}

@Composable
fun RequestBuilder(
    uiState: HomeUiState,
    callbacks: HomeCallbacks,
    modifier: Modifier = Modifier,
) {
    val statusCode: Int? = (uiState.response as? Loadable.Success)?.data?.statusCode
    var isSearchVisible by remember { mutableStateOf(false) }

    val cardModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 4.dp)
        .border(0.5.dp, MaterialTheme.colorScheme.cardBorder, RoundedCornerShape(12.dp))
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.cardBackground)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RequestLine(
                httpMethods = HTTP_METHODS,
                selectedHttpMethod = uiState.data.httpMethod,
                requestUrl = uiState.data.requestUrl,
                onHttpMethodChanged = callbacks.onHttpMethodChanged,
                onRequestUrlChanged = callbacks.onRequestUrlChanged,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = callbacks.onSendRequestClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Send,
                    contentDescription = "Send Request",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        RequestParametersSection(
            modifier = cardModifier,
            headers = uiState.data.headers,
            params = uiState.data.params,
            body = uiState.data.body,
            callbacks = callbacks
        )

        Column(modifier = cardModifier.weight(1f)) {
            ResponseBodyTopBar(
                statusCode = statusCode,
                onSearchToggle = { isSearchVisible = !isSearchVisible },
                callbacks = callbacks
            )
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            ResponseBody(
                response = uiState.response,
                isSearchVisible = isSearchVisible,
                onDismissSearch = { isSearchVisible = false }
            )
        }
    }
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
    var isHttpMethodExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box {
            Surface(
                onClick = { isHttpMethodExpanded = true },
                shape = RoundedCornerShape(10.dp),
                color = selectedHttpMethod.color.copy(alpha = MaterialTheme.colorScheme.chipTintAlpha),
                modifier = Modifier.width(86.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedHttpMethod.name,
                        color = selectedHttpMethod.color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Icon(
                        imageVector = TriangleDown,
                        contentDescription = null,
                        tint = selectedHttpMethod.color.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = isHttpMethodExpanded,
                onDismissRequest = { isHttpMethodExpanded = false },
                shape = RoundedCornerShape(12.dp),
                containerColor = MaterialTheme.colorScheme.cardBackground,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.dropdownBorder),
                shadowElevation = 0.dp,
            ) {
                httpMethods.forEach { option ->
                    val isSelected = option == selectedHttpMethod
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(option.color, RoundedCornerShape(4.dp))
                                )
                                Text(
                                    text = option.name,
                                    color = option.color,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        },
                        modifier = Modifier.background(
                            if (isSelected) option.color.copy(alpha = 0.1f) else Color.Transparent
                        ),
                        onClick = {
                            isHttpMethodExpanded = false
                            onHttpMethodChanged(option)
                        }
                    )
                }
            }
        }

        TextField(
            value = requestUrl,
            onValueChange = onRequestUrlChanged,
            singleLine = true,
            placeholder = { Text("Enter URL...", color = MaterialTheme.colorScheme.textMuted, fontSize = 13.sp) },
            modifier = Modifier
                .weight(1f)
                .border(0.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(10.dp),
            colors = inputFieldColors()
        )
    }
}

@Composable
fun RequestParametersSection(
    modifier: Modifier,
    headers: List<KeyValue>?,
    params: List<KeyValue>?,
    body: String?,
    callbacks: HomeCallbacks,
) {
    var selectedOption by rememberSaveable { mutableStateOf(HTTP_PARAM_OPTIONS[0]) }

    Column(modifier = modifier.fillMaxWidth()) {
        HttpParameterSelection(HTTP_PARAM_OPTIONS, selectedOption) { selectedOption = it }
        HttpParameterBody(selectedOption, headers, params, body, callbacks)
    }
}

@Composable
private fun HttpParameterSelection(
    options: List<RadioHttpParameterOptions>,
    selectedOption: RadioHttpParameterOptions,
    onOptionSelected: (RadioHttpParameterOptions) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            val label = if (option == RadioHttpParameterOptions.Header) "Headers" else option.name
            Column(
                modifier = Modifier
                    .clickable { onOptionSelected(option) }
                    .width(IntrinsicSize.Max),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.textMuted,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(1.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun HttpParameterBody(
    selectedOption: RadioHttpParameterOptions,
    headers: List<KeyValue>?,
    params: List<KeyValue>?,
    body: String?,
    callbacks: HomeCallbacks,
) {
    Box(
        modifier = Modifier
            .height(120.dp)
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        when (selectedOption) {
            RadioHttpParameterOptions.Auth -> AuthSection(
                Modifier.padding(start = 12.dp, end = 12.dp, top = 6.dp), headers, callbacks
            )

            RadioHttpParameterOptions.Params -> ParamsSection(params, callbacks)
            RadioHttpParameterOptions.Header -> HeaderSection(headers, callbacks)
            RadioHttpParameterOptions.Body -> HttpParameterBodySection(
                Modifier.fillMaxSize(),
                body,
                callbacks
            )
        }
    }
}

@Composable
private fun StatusCode(statusCode: Int?) {
    if (statusCode == null) return
    val isSuccess = statusCode in 200..208
    val textColor =
        if (isSuccess) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.error
    val bgColor =
        if (isSuccess) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.errorContainer

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Status ",
            modifier = Modifier.padding(start = 24.dp),
            color = MaterialTheme.colorScheme.textMuted,
            fontSize = 12.sp
        )
        Text(
            text = statusCode.toString(),
            color = textColor,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(bgColor)
                .padding(horizontal = 4.dp)
        )
    }
}

@Composable
private fun KeyValueSection(
    items: List<KeyValue>?,
    onAdd: (String, String) -> Unit,
    onRemove: (String, String) -> Unit,
) {
    Column {
        KeyValueInput { key, value -> onAdd(key, value) }
        Spacer(modifier = Modifier.height(4.dp))
        RemovableTagList(items = items, onRemoveItem = onRemove)
    }
}

@Composable
fun ParamsSection(params: List<KeyValue>?, callbacks: HomeCallbacks) {
    KeyValueSection(
        items = params,
        onAdd = callbacks.onAddParameter,
        onRemove = callbacks.onRemoveParameter,
    )
}

@Composable
fun AuthSection(
    modifier: Modifier,
    headers: List<KeyValue>?,
    callbacks: HomeCallbacks,
) {
    Column(modifier = modifier) {
        Text(
            text = "Bearer Token",
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        TextVisibilityTextField(
            value = headers?.getHeaderValue("Authorization") ?: "",
            onTextChange = { callbacks.onAddHeader("Authorization", it) }
        )
    }
}

@Composable
fun HeaderSection(headers: List<KeyValue>?, callbacks: HomeCallbacks) {
    KeyValueSection(
        items = headers,
        onAdd = callbacks.onAddHeader,
        onRemove = callbacks.onRemoveHeader,
    )
}

@Composable
fun HttpParameterBodySection(
    modifier: Modifier,
    body: String?,
    callbacks: HomeCallbacks,
) {
    TextField(
        value = body ?: "",
        onValueChange = callbacks.onBodyChanged,
        maxLines = Int.MAX_VALUE,
        placeholder = {
            Text(
                "Enter request body...",
                color = MaterialTheme.colorScheme.textMuted,
                fontSize = 12.sp
            )
        },
        modifier = modifier.padding(8.dp),
        shape = RoundedCornerShape(10.dp),
        colors = inputFieldColors()
    )
}

@Composable
private fun ResponseBodyTopBar(
    statusCode: Int?,
    onSearchToggle: () -> Unit,
    callbacks: HomeCallbacks,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Response",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.textMuted,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = "{ } JSON",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.textMuted,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.jsonChipBackground, RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colorScheme.jsonChipBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp)
        )
        StatusCode(statusCode)
        Spacer(Modifier.weight(1f))
        IconButton(
            onClick = onSearchToggle,
            modifier = Modifier
                .padding(end = 8.dp)
                .size(18.dp)
        ) {
            Icon(
                imageVector = Search,
                contentDescription = "search",
                tint = MaterialTheme.colorScheme.textMuted
            )
        }
        IconButton(onClick = callbacks.onCopyClick, modifier = Modifier.size(18.dp)) {
            Icon(
                imageVector = Content_copy,
                contentDescription = "copy response",
                tint = MaterialTheme.colorScheme.textMuted
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
                    modifier = Modifier.fillMaxWidth(),
                    alignment = Alignment.Center,
                )
            }
        }

        is Loadable.Error -> ResponseErrorText(response.message)
        is Loadable.NetworkError -> ResponseErrorText(response.message)
        Loadable.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        is Loadable.Empty -> Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(R.drawable.action_block),
                contentDescription = "no responses yet",
                tint = MaterialTheme.colorScheme.iconOnBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enter the URL and click send to get a response",
                color = MaterialTheme.colorScheme.textMuted,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun ResponseErrorText(message: String) {
    Text(
        text = "Error: $message",
        modifier = Modifier.padding(16.dp),
        color = Color.Red
    )
}
