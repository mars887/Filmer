package com.example.sql_module

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [DatabaseModule::class]
)
interface SqlComponent : SqlProvider {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): SqlComponent
    }
}
