<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Concerns\HasUuids;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class ShareLink extends Model
{
    use HasFactory, HasUuids;

    protected $fillable = [
        'user_id',
        'item_id',
        'encrypted_item_blob',
        'expires_at',
        'max_views',
        'view_count',
    ];

    protected $casts = [
        'expires_at' => 'datetime',
        'max_views' => 'integer',
        'view_count' => 'integer',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function vaultItem()
    {
        return $this->belongsTo(VaultItem::class, 'item_id');
    }

    public function isExpired(): bool
    {
        if ($this->expires_at && $this->expires_at->isPast()) {
            return true;
        }

        if ($this->max_views && $this->view_count >= $this->max_views) {
            return true;
        }

        return false;
    }
}
