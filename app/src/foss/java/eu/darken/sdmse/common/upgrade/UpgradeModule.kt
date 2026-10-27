package eu.darken.sdmse.common.upgrade

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import eu.darken.sdmse.common.upgrade.core.UpgradeDiagnosticsFoss
import eu.darken.sdmse.common.upgrade.core.UpgradeRepoUnlocked
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class UpgradeModule {
    @Binds
    @Singleton
    abstract fun control(unlocked: UpgradeRepoUnlocked): UpgradeRepo

    @Binds
    @Singleton
    abstract fun diagnostics(foss: UpgradeDiagnosticsFoss): UpgradeDiagnostics

}