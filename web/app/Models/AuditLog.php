<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Concerns\HasUuids;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class AuditLog extends Model
{
    use HasFactory, HasUuids;

    public $timestamps = false;

    protected $fillable = [
        'user_id',
        'action',
        'item_id',
        'item_type',
        'ip_address',
        'user_agent',
        'performed_at',
    ];

    protected $casts = [
        'performed_at' => 'integer',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }
}
