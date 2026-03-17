package com.ivarna.truvalt;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.ivarna.truvalt.core.crypto.CryptoManager;
import com.ivarna.truvalt.data.local.dao.FolderDao;
import com.ivarna.truvalt.data.local.dao.TagDao;
import com.ivarna.truvalt.data.local.dao.VaultItemDao;
import com.ivarna.truvalt.data.local.database.TruvaltDatabase;
import com.ivarna.truvalt.data.preferences.TruvaltPreferences;
import com.ivarna.truvalt.data.repository.AuthRepositoryImpl;
import com.ivarna.truvalt.data.repository.SyncRepositoryImpl;
import com.ivarna.truvalt.data.repository.VaultRepositoryImpl;
import com.ivarna.truvalt.di.AppModule_ProvideCryptoManagerFactory;
import com.ivarna.truvalt.di.AppModule_ProvideTruvaltPreferencesFactory;
import com.ivarna.truvalt.di.DatabaseModule_ProvideDatabaseFactory;
import com.ivarna.truvalt.di.DatabaseModule_ProvideFolderDaoFactory;
import com.ivarna.truvalt.di.DatabaseModule_ProvideTagDaoFactory;
import com.ivarna.truvalt.di.DatabaseModule_ProvideVaultItemDaoFactory;
import com.ivarna.truvalt.presentation.MainActivity;
import com.ivarna.truvalt.presentation.ui.auth.AuthViewModel;
import com.ivarna.truvalt.presentation.ui.auth.AuthViewModel_HiltModules;
import com.ivarna.truvalt.presentation.ui.auth.AuthViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.auth.AuthViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.auth.LoginViewModel;
import com.ivarna.truvalt.presentation.ui.auth.LoginViewModel_HiltModules;
import com.ivarna.truvalt.presentation.ui.auth.LoginViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.auth.LoginViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.auth.RegisterViewModel;
import com.ivarna.truvalt.presentation.ui.auth.RegisterViewModel_HiltModules;
import com.ivarna.truvalt.presentation.ui.auth.RegisterViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.auth.RegisterViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.auth.ServerSetupViewModel;
import com.ivarna.truvalt.presentation.ui.auth.ServerSetupViewModel_HiltModules;
import com.ivarna.truvalt.presentation.ui.auth.ServerSetupViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.auth.ServerSetupViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.health.HealthViewModel;
import com.ivarna.truvalt.presentation.ui.health.HealthViewModel_HiltModules;
import com.ivarna.truvalt.presentation.ui.health.HealthViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.health.HealthViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.settings.SettingsViewModel;
import com.ivarna.truvalt.presentation.ui.settings.SettingsViewModel_HiltModules;
import com.ivarna.truvalt.presentation.ui.settings.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.settings.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.vault.VaultItemEditViewModel;
import com.ivarna.truvalt.presentation.ui.vault.VaultItemEditViewModel_HiltModules;
import com.ivarna.truvalt.presentation.ui.vault.VaultItemEditViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.vault.VaultItemEditViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.vault.VaultViewModel;
import com.ivarna.truvalt.presentation.ui.vault.VaultViewModel_HiltModules;
import com.ivarna.truvalt.presentation.ui.vault.VaultViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ivarna.truvalt.presentation.ui.vault.VaultViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DaggerTruvaltApp_HiltComponents_SingletonC {
  private DaggerTruvaltApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public TruvaltApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements TruvaltApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public TruvaltApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements TruvaltApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public TruvaltApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements TruvaltApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public TruvaltApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements TruvaltApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public TruvaltApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements TruvaltApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public TruvaltApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements TruvaltApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public TruvaltApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements TruvaltApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public TruvaltApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends TruvaltApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends TruvaltApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends TruvaltApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends TruvaltApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(8).put(AuthViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AuthViewModel_HiltModules.KeyModule.provide()).put(HealthViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, HealthViewModel_HiltModules.KeyModule.provide()).put(LoginViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, LoginViewModel_HiltModules.KeyModule.provide()).put(RegisterViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, RegisterViewModel_HiltModules.KeyModule.provide()).put(ServerSetupViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, ServerSetupViewModel_HiltModules.KeyModule.provide()).put(SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide()).put(VaultItemEditViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, VaultItemEditViewModel_HiltModules.KeyModule.provide()).put(VaultViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, VaultViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends TruvaltApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AuthViewModel> authViewModelProvider;

    private Provider<HealthViewModel> healthViewModelProvider;

    private Provider<LoginViewModel> loginViewModelProvider;

    private Provider<RegisterViewModel> registerViewModelProvider;

    private Provider<ServerSetupViewModel> serverSetupViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<VaultItemEditViewModel> vaultItemEditViewModelProvider;

    private Provider<VaultViewModel> vaultViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.healthViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.loginViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.registerViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.serverSetupViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.vaultItemEditViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.vaultViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(8).put(AuthViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) authViewModelProvider)).put(HealthViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) healthViewModelProvider)).put(LoginViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) loginViewModelProvider)).put(RegisterViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) registerViewModelProvider)).put(ServerSetupViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) serverSetupViewModelProvider)).put(SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) settingsViewModelProvider)).put(VaultItemEditViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) vaultItemEditViewModelProvider)).put(VaultViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) vaultViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.ivarna.truvalt.presentation.ui.auth.AuthViewModel 
          return (T) new AuthViewModel(singletonCImpl.authRepositoryImplProvider.get(), singletonCImpl.syncRepositoryImplProvider.get(), singletonCImpl.provideTruvaltPreferencesProvider.get());

          case 1: // com.ivarna.truvalt.presentation.ui.health.HealthViewModel 
          return (T) new HealthViewModel(singletonCImpl.vaultRepositoryImplProvider.get());

          case 2: // com.ivarna.truvalt.presentation.ui.auth.LoginViewModel 
          return (T) new LoginViewModel(singletonCImpl.authRepositoryImplProvider.get(), singletonCImpl.vaultRepositoryImplProvider.get());

          case 3: // com.ivarna.truvalt.presentation.ui.auth.RegisterViewModel 
          return (T) new RegisterViewModel(singletonCImpl.authRepositoryImplProvider.get());

          case 4: // com.ivarna.truvalt.presentation.ui.auth.ServerSetupViewModel 
          return (T) new ServerSetupViewModel(singletonCImpl.syncRepositoryImplProvider.get());

          case 5: // com.ivarna.truvalt.presentation.ui.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.provideTruvaltPreferencesProvider.get(), singletonCImpl.authRepositoryImplProvider.get(), singletonCImpl.syncRepositoryImplProvider.get());

          case 6: // com.ivarna.truvalt.presentation.ui.vault.VaultItemEditViewModel 
          return (T) new VaultItemEditViewModel(singletonCImpl.vaultRepositoryImplProvider.get());

          case 7: // com.ivarna.truvalt.presentation.ui.vault.VaultViewModel 
          return (T) new VaultViewModel(singletonCImpl.vaultRepositoryImplProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends TruvaltApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends TruvaltApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends TruvaltApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<CryptoManager> provideCryptoManagerProvider;

    private Provider<TruvaltPreferences> provideTruvaltPreferencesProvider;

    private Provider<AuthRepositoryImpl> authRepositoryImplProvider;

    private Provider<SyncRepositoryImpl> syncRepositoryImplProvider;

    private Provider<TruvaltDatabase> provideDatabaseProvider;

    private Provider<VaultRepositoryImpl> vaultRepositoryImplProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private VaultItemDao vaultItemDao() {
      return DatabaseModule_ProvideVaultItemDaoFactory.provideVaultItemDao(provideDatabaseProvider.get());
    }

    private FolderDao folderDao() {
      return DatabaseModule_ProvideFolderDaoFactory.provideFolderDao(provideDatabaseProvider.get());
    }

    private TagDao tagDao() {
      return DatabaseModule_ProvideTagDaoFactory.provideTagDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideCryptoManagerProvider = DoubleCheck.provider(new SwitchingProvider<CryptoManager>(singletonCImpl, 1));
      this.provideTruvaltPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<TruvaltPreferences>(singletonCImpl, 2));
      this.authRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepositoryImpl>(singletonCImpl, 0));
      this.syncRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<SyncRepositoryImpl>(singletonCImpl, 3));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<TruvaltDatabase>(singletonCImpl, 5));
      this.vaultRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<VaultRepositoryImpl>(singletonCImpl, 4));
    }

    @Override
    public void injectTruvaltApp(TruvaltApp truvaltApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.ivarna.truvalt.data.repository.AuthRepositoryImpl 
          return (T) new AuthRepositoryImpl(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideCryptoManagerProvider.get(), singletonCImpl.provideTruvaltPreferencesProvider.get());

          case 1: // com.ivarna.truvalt.core.crypto.CryptoManager 
          return (T) AppModule_ProvideCryptoManagerFactory.provideCryptoManager();

          case 2: // com.ivarna.truvalt.data.preferences.TruvaltPreferences 
          return (T) AppModule_ProvideTruvaltPreferencesFactory.provideTruvaltPreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.ivarna.truvalt.data.repository.SyncRepositoryImpl 
          return (T) new SyncRepositoryImpl(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.provideTruvaltPreferencesProvider.get());

          case 4: // com.ivarna.truvalt.data.repository.VaultRepositoryImpl 
          return (T) new VaultRepositoryImpl(singletonCImpl.vaultItemDao(), singletonCImpl.folderDao(), singletonCImpl.tagDao(), singletonCImpl.provideCryptoManagerProvider.get(), singletonCImpl.provideTruvaltPreferencesProvider.get());

          case 5: // com.ivarna.truvalt.data.local.database.TruvaltDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
