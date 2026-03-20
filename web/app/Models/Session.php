<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Concerns\HasUuids;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Session extends Model
{
    use HasFactory, HasUuids;

    protected $fillable = [
        'user_id',
        'device_name',
        'ip_address',
        'user_agent',
        'last_active_at',
    ];

    protected $casts = [
        'last_active_at' => 'integer',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}
