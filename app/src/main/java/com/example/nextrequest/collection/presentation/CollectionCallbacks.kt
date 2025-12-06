package com.example.nextrequest.collection.presentation

import com.example.nextrequest.collection.domain.model.Collection

data class CollectionCallbacks(
    val onCollectionItemClick: (Int, String) -> Unit,
    val onRenameRequestClick: (Int, String) -> Unit,
    val onRenameCollectionClick: (Collection) -> Unit,
    val onCreateEmptyRequestClick: (String) -> Unit,
    val onCreateNewCollectionClick: () -> Unit,
    val onHeaderClick: (String) -> Unit,
    val onDeleteCollectionClick: (String) -> Unit,
    val onDeleteRequestClick: (Int) -> Unit,
)