package eu.darken.sdmse.systemcleaner.core.filter.stock

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.DeleteSweep
import androidx.compose.ui.graphics.vector.ImageVector
import dagger.Binds
import dagger.Module
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import eu.darken.sdmse.systemcleaner.R
import eu.darken.sdmse.common.areas.DataArea
import eu.darken.sdmse.common.ca.CaString
import eu.darken.sdmse.common.ca.toCaString
import eu.darken.sdmse.common.datastore.value
import eu.darken.sdmse.common.debug.logging.log
import eu.darken.sdmse.common.debug.logging.logTag
import eu.darken.sdmse.common.files.APathLookup
import eu.darken.sdmse.common.files.GatewaySwitch
import eu.darken.sdmse.common.sieve.NameCriterium
import eu.darken.sdmse.common.sieve.TypeCriterium
import eu.darken.sdmse.systemcleaner.core.SystemCleanerSettings
import eu.darken.sdmse.systemcleaner.core.filter.BaseSystemCleanerFilter
import eu.darken.sdmse.systemcleaner.core.filter.SystemCleanerFilter
import eu.darken.sdmse.systemcleaner.core.filter.toDeletion
import eu.darken.sdmse.systemcleaner.core.sieve.SystemCrawlerSieve
import javax.inject.Inject
import javax.inject.Provider

/**
 * Local build modification: discovers recycle-bin/trash folders from various file managers,
 * gallery apps and operating systems, including everything stored inside them.
 */
class TrashBinsFilter @Inject constructor(
    private val sieveFactory: SystemCrawlerSieve.Factory,
    private val gatewaySwitch: GatewaySwitch,
) : BaseSystemCleanerFilter() {

    override val icon: ImageVector = Icons.TwoTone.DeleteSweep

    override suspend fun getLabel(): CaString = R.string.systemcleaner_filter_trashbins_label.toCaString()

    override suspend fun getDescription(): CaString {
        return R.string.systemcleaner_filter_trashbins_summary.toCaString()
    }

    override suspend fun targetAreas(): Set<DataArea.Type> = setOf(
        DataArea.Type.SDCARD,
        DataArea.Type.PORTABLE,
    )

    private lateinit var sieve: SystemCrawlerSieve

    override suspend fun initialize() {
        val config = SystemCrawlerSieve.Config(
            targetTypes = setOf(TypeCriterium.DIRECTORY),
            areaTypes = targetAreas(),
            nameCriteria = setOf(
                // .Trash, .Trash-0..999, .trash (MIUI), .Trashes (macOS)
                NameCriterium(".Trash", mode = NameCriterium.Mode.Start()),
                // .recycle, .recyclebin, .RecycleBin
                NameCriterium(".recycle", mode = NameCriterium.Mode.Start()),
                // Windows recycle bin folders
                NameCriterium("\$RECYCLE.BIN", mode = NameCriterium.Mode.Equal()),
            ),
        )
        sieve = sieveFactory.create(config)
        log(TAG) { "initialized() with $config" }
    }

    override suspend fun match(item: APathLookup<*>): SystemCleanerFilter.Match? {
        return sieve.match(item).toDeletion()
    }

    override suspend fun process(
        matches: Collection<SystemCleanerFilter.Match>
    ): Collection<SystemCleanerFilter.Processed> {
        return matches.filterIsInstance<SystemCleanerFilter.Match.Deletion>().deleteAll(gatewaySwitch)
    }

    override fun toString(): String = "${this::class.simpleName}(${hashCode()})"

    @Reusable
    class Factory @Inject constructor(
        private val settings: SystemCleanerSettings,
        private val filterProvider: Provider<TrashBinsFilter>
    ) : SystemCleanerFilter.Factory {
        override suspend fun isEnabled(): Boolean = settings.filterTrashBinsEnabled.value()
        override suspend fun create(): SystemCleanerFilter = filterProvider.get()
    }

    @InstallIn(SingletonComponent::class)
    @Module
    abstract class DIM {
        @Binds @IntoSet abstract fun mod(mod: Factory): SystemCleanerFilter.Factory
    }

    companion object {
        private val TAG = logTag("SystemCleaner", "Filter", "TrashBins")
    }
}
