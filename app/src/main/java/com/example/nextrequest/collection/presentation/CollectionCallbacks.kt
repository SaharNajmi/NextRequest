package com.example.nextrequest.collection.presentation

import com.example.nextrequest.collection.domain.model.CollectionItem
import com.example.nextrequest.collection.domain.model.RequestCollection

data class CollectionCallbacks(
    val onCollectionItemClick: (CollectionItem, String) -> Unit,
    val onRenameRequestClick: (Int, String) -> Unit,
    val onRenameCollectionClick: (RequestCollection) -> Unit,
    val onCreateEmptyRequestClick: (String) -> Unit,
    val onCreateNewCollectionClick: () -> Unit,
    val onHeaderClick: (String) -> Unit,
    val onDeleteCollectionClick: (String) -> Unit,
    val onDeleteRequestClick: (Int) -> Unit,
)