<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Concerns\HasUuids;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class VaultItem extends Model
{
    use HasFactory, HasUuids;

    public $timestamps = false;

    protected $fillable = [
        'user_id',
        'type',
        'name',
        'folder_id',
        'encrypted_data',
        'favorite',
        'created_at',
        'updated_at',
        'deleted_at',
    ];

    protected $casts = [
        'favorite' => 'boolean',
        'created_at' => 'integer',
        'updated_at' => 'integer',
        'deleted_at' => 'integer',
    ];

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function folder()
    {
        return $this->belongsTo(Folder::class);
    }

    public function tags()
    {
        return $this->belongsToMany(Tag::class, 'vault_item_tags', 'item_id', 'tag_id');
    }

    public function scopeNotDeleted($query)
    {
        return $query->whereNull('deleted_at');
    }

    public function scopeDeleted($query)
    {
        return $query->whereNotNull('deleted_at');
    }
}
