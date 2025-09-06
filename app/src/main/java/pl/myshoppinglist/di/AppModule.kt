package pl.myshoppinglist.di

import android.content.Context
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.myshoppinglist.data.local.AppDatabase
import pl.myshoppinglist.domain.repo.ShoppingRepository
import pl.myshoppinglist.data.repo.ShoppingRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindModule {
    @Binds
    abstract fun bindRepo(impl: ShoppingRepositoryImpl): ShoppingRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "shopping.db").build()

    @Provides fun provideListDao(db: AppDatabase) = db.shoppingListDao()
    @Provides fun provideItemDao(db: AppDatabase) = db.shoppingItemDao()
    @Provides fun provideCategoryDao(db: AppDatabase) = db.categoryDao()
}
