<?php

namespace App\Providers;

use App\Services\Firebase\FirebaseAdmin;
use App\Services\Firebase\FirebaseAuthService;
use App\Services\Firebase\FirebaseProjectConfig;
use App\Services\Firebase\FirestoreRestClient;
use App\Services\Firebase\IdentityToolkitClient;
use App\Services\Firebase\TruvaltFirestoreRepository;
use Illuminate\Support\ServiceProvider;

class AppServiceProvider extends ServiceProvider
{
    /**
     * Register any application services.
     */
    public function register(): void
    {
        $this->app->singleton(FirebaseProjectConfig::class);
        $this->app->singleton(FirebaseAdmin::class);
        $this->app->singleton(IdentityToolkitClient::class);
        $this->app->singleton(FirestoreRestClient::class);
        $this->app->singleton(TruvaltFirestoreRepository::class);
        $this->app->singleton(FirebaseAuthService::class);
    }

    /**
     * Bootstrap any application services.
     */
    public function boot(): void
    {
        //
    }
}
