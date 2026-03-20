<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Concerns\HasUuids;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Passkey extends Model
{
    use HasFactory, HasUuids;

    protected $fillable = [
        'user_id',
        'credential_id',
        'public_key',
        'sign_count',
        'device_name',
    ];

    protected $casts = [
        'sign_count' => 'integer',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}
