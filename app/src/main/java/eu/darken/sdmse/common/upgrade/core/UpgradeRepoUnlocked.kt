package eu.darken.sdmse.common.upgrade.core

import eu.darken.sdmse.common.debug.logging.log
import eu.darken.sdmse.common.debug.logging.logTag
import eu.darken.sdmse.common.upgrade.UpgradeRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Local build modification: all Pro features are permanently unlocked.
 * No billing, no license verification, no upgrade prompts.
 */
@Singleton
class UpgradeRepoUnlocked @Inject constructor() : UpgradeRepo {

    override val storeSite: String = "https://github.com/d4rken-org/sdmaid-se"
    override val upgradeSite: String = "https://github.com/d4rken-org/sdmaid-se"
    override val betaSite: String = "https://github.com/d4rken-org/sdmaid-se/releases"

    override val upgradeInfo: Flow<UpgradeRepo.Info> = flowOf(Info())

    override suspend fun refresh() {
        log(TAG) { "refresh(): nothing to do, everything is unlocked" }
    }

    override suspend fun onAppStart() {
        log(TAG) { "onAppStart(): nothing to schedule, everything is unlocked" }
    }

    data class Info(
        override val isPro: Boolean = true,
        override val upgradedAt: java.time.Instant? = null,
        override val error: Throwable? = null,
    ) : UpgradeRepo.Info {
        override val type: UpgradeRepo.Type = UpgradeRepo.Type.FOSS
        override val isSettled: Boolean = true
    }

    companion object {
        private val TAG = logTag("Upgrade", "Unlocked", "Repo")
    }
}
