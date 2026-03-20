<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Concerns\HasUuids;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    use HasApiTokens, HasFactory, HasUuids, Notifiable;

    protected $fillable = [
        'email',
        'auth_key_hash',
        'two_factor_secret',
        'two_factor_confirmed_at',
    ];

    protected $hidden = [
        'auth_key_hash',
        'two_factor_secret',
    ];

    protected $casts = [
        'two_factor_confirmed_at' => 'datetime',
    ];

    public function vaultItems()
    {
        return $this->hasMany(VaultItem::class);
    }

    public function folders()
    {
        return $this->hasMany(Folder::class);
    }

    public function tags()
    {
        return $this->hasMany(Tag::class);
    }

    public function sessions()
    {
        return $this->hasMany(Session::class);
    }

    public function auditLogs()
    {
        return $this->hasMany(AuditLog::class);
    }

    public function passkeys()
    {
        return $this->hasMany(Passkey::class);
    }

    public function shareLinks()
    {
        return $this->hasMany(ShareLink::class);
    }

    public function devices()
    {
        return $this->hasMany(Device::class);
    }
}
