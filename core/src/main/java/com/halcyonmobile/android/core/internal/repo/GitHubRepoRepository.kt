package com.halcyonmobile.android.core.internal.repo

import com.halcyonmobile.android.core.internal.api.GitHubRepoRemoteSource
import com.halcyonmobile.android.core.internal.localsource.GitHubRepoLocalSource
import com.halcyonmobile.android.core.model.GitHubRepo
import kotlinx.coroutines.flow.Flow

internal class GitHubRepoRepository(
    private val remoteSource: GitHubRepoRemoteSource,
    private val localSource: GitHubRepoLocalSource
) {

    suspend fun fetch(numberOfElements: Int): Flow<List<GitHubRepo>> {
        localSource.clearCache()
        val dataLoaded = remoteSource.getReposPaginated(page = 1, perPage = numberOfElements)
        localSource.addToCache(dataLoaded)

        return localSource.getFirstElements(numberOfElements)
    }

    suspend fun get(numberOfElements: Int): Flow<List<GitHubRepo>> {
        val numberOfElementsCached = localSource.numberOfElementsCached()
        if (numberOfElementsCached < numberOfElements) {
            val dataLoaded = remoteSource.getReposPaginated(
                page = numberOfElements / (numberOfElements - numberOfElementsCached),
                perPage = numberOfElements - numberOfElementsCached
            )
            localSource.addToCache(dataLoaded)
        }

        return localSource.getFirstElements(numberOfElements)
    }
}