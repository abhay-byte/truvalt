<?php

namespace App\Services\Firebase;

use Illuminate\Support\Str;

class TruvaltFirestoreRepository
{
    public function __construct(
        private readonly FirestoreRestClient $firestore,
    ) {
    }

    public function getUserProfile(string $uid): ?array
    {
        $profile = $this->firestore->getDocument($this->userProfilePath($uid));

        return $profile === null ? null : $this->sanitizeUserProfile($profile);
    }

    public function getRawUserProfile(string $uid): ?array
    {
        return $this->firestore->getDocument($this->userProfilePath($uid));
    }

    public function saveUserProfile(string $uid, array $attributes): array
    {
        $existing = $this->getRawUserProfile($uid) ?? [];
        $timestamp = now()->timestamp;

        $profile = array_replace($existing, $attributes, [
            'id' => $uid,
            'updated_at' => $timestamp,
            'created_at' => $existing['created_at'] ?? $timestamp,
        ]);

        return $this->sanitizeUserProfile(
            $this->firestore->setDocument($this->userProfilePath($uid), $profile)
        );
    }

    public function listVaultItems(string $uid): array
    {
        $items = $this->firestore->listDocuments($this->vaultItemsCollectionPath($uid));

        return array_map(fn (array $item) => $this->normalizeVaultItem($uid, $item), $items);
    }

    public function getVaultItem(string $uid, string $itemId): ?array
    {
        $item = $this->firestore->getDocument($this->vaultItemPath($uid, $itemId));

        return $item === null ? null : $this->normalizeVaultItem($uid, $item);
    }

    public function saveVaultItem(string $uid, array $attributes): array
    {
        $itemId = (string) ($attributes['id'] ?? Str::uuid());
        $existing = $this->getVaultItem($uid, $itemId) ?? [];
        $timestamp = now()->timestamp;

        $item = array_replace($existing, $attributes, [
            'id' => $itemId,
            'user_id' => $uid,
            'created_at' => $existing['created_at'] ?? $attributes['created_at'] ?? $timestamp,
            'updated_at' => $attributes['updated_at'] ?? $timestamp,
        ]);

        return $this->normalizeVaultItem(
            $uid,
            $this->firestore->setDocument($this->vaultItemPath($uid, $itemId), $item)
        );
    }

    public function deleteVaultItem(string $uid, string $itemId): void
    {
        $this->firestore->deleteDocument($this->vaultItemPath($uid, $itemId));
    }

    public function listFolders(string $uid): array
    {
        $folders = $this->firestore->listDocuments($this->foldersCollectionPath($uid));

        return array_map(fn (array $folder) => $this->normalizeFolder($uid, $folder), $folders);
    }

    public function getFolder(string $uid, string $folderId): ?array
    {
        $folder = $this->firestore->getDocument($this->folderPath($uid, $folderId));

        return $folder === null ? null : $this->normalizeFolder($uid, $folder);
    }

    public function saveFolder(string $uid, array $attributes): array
    {
        $folderId = (string) ($attributes['id'] ?? Str::uuid());
        $existing = $this->getFolder($uid, $folderId) ?? [];

        $folder = array_replace($existing, $attributes, [
            'id' => $folderId,
            'user_id' => $uid,
            'updated_at' => $attributes['updated_at'] ?? now()->timestamp,
        ]);

        return $this->normalizeFolder(
            $uid,
            $this->firestore->setDocument($this->folderPath($uid, $folderId), $folder)
        );
    }

    public function deleteFolder(string $uid, string $folderId): void
    {
        foreach ($this->listVaultItems($uid) as $item) {
            if (($item['folder_id'] ?? null) === $folderId) {
                $this->saveVaultItem($uid, [
                    'id' => $item['id'],
                    'folder_id' => null,
                    'updated_at' => now()->timestamp,
                ]);
            }
        }

        foreach ($this->listFolders($uid) as $folder) {
            if (($folder['parent_id'] ?? null) === $folderId) {
                $this->saveFolder($uid, [
                    'id' => $folder['id'],
                    'parent_id' => null,
                    'updated_at' => now()->timestamp,
                ]);
            }
        }

        $this->firestore->deleteDocument($this->folderPath($uid, $folderId));
    }

    public function listTags(string $uid): array
    {
        $tags = $this->firestore->listDocuments($this->tagsCollectionPath($uid));

        return array_map(fn (array $tag) => $this->normalizeTag($uid, $tag), $tags);
    }

    public function getTag(string $uid, string $tagId): ?array
    {
        $tag = $this->firestore->getDocument($this->tagPath($uid, $tagId));

        return $tag === null ? null : $this->normalizeTag($uid, $tag);
    }

    public function saveTag(string $uid, array $attributes): array
    {
        $tagId = (string) ($attributes['id'] ?? Str::uuid());
        $existing = $this->getTag($uid, $tagId) ?? [];

        $tag = array_replace($existing, $attributes, [
            'id' => $tagId,
            'user_id' => $uid,
        ]);

        return $this->normalizeTag(
            $uid,
            $this->firestore->setDocument($this->tagPath($uid, $tagId), $tag)
        );
    }

    public function deleteTag(string $uid, string $tagId): void
    {
        $this->firestore->deleteDocument($this->tagPath($uid, $tagId));
    }

    private function sanitizeUserProfile(array $profile): array
    {
        return [
            'id' => (string) ($profile['id'] ?? ''),
            'email' => (string) ($profile['email'] ?? ''),
            'providers' => array_values($profile['providers'] ?? []),
            'auth_key_hash_configured' => isset($profile['auth_key_hash']) && $profile['auth_key_hash'] !== '',
            'created_at' => (int) ($profile['created_at'] ?? now()->timestamp),
            'updated_at' => (int) ($profile['updated_at'] ?? now()->timestamp),
            'last_login_at' => isset($profile['last_login_at']) ? (int) $profile['last_login_at'] : null,
            'email_verified' => (bool) ($profile['email_verified'] ?? false),
        ];
    }

    private function normalizeVaultItem(string $uid, array $item): array
    {
        return [
            'id' => (string) ($item['id'] ?? ''),
            'user_id' => $uid,
            'type' => (string) ($item['type'] ?? ''),
            'name' => (string) ($item['name'] ?? ''),
            'folder_id' => isset($item['folder_id']) && $item['folder_id'] !== '' ? (string) $item['folder_id'] : null,
            'encrypted_data' => isset($item['encrypted_data']) ? (string) $item['encrypted_data'] : null,
            'favorite' => (bool) ($item['favorite'] ?? false),
            'created_at' => (int) ($item['created_at'] ?? now()->timestamp),
            'updated_at' => (int) ($item['updated_at'] ?? now()->timestamp),
            'deleted_at' => isset($item['deleted_at']) ? (int) $item['deleted_at'] : null,
        ];
    }

    private function normalizeFolder(string $uid, array $folder): array
    {
        return [
            'id' => (string) ($folder['id'] ?? ''),
            'user_id' => $uid,
            'name' => (string) ($folder['name'] ?? ''),
            'icon' => isset($folder['icon']) ? (string) $folder['icon'] : null,
            'parent_id' => isset($folder['parent_id']) && $folder['parent_id'] !== '' ? (string) $folder['parent_id'] : null,
            'updated_at' => (int) ($folder['updated_at'] ?? now()->timestamp),
        ];
    }

    private function normalizeTag(string $uid, array $tag): array
    {
        return [
            'id' => (string) ($tag['id'] ?? ''),
            'user_id' => $uid,
            'name' => (string) ($tag['name'] ?? ''),
        ];
    }

    private function userProfilePath(string $uid): string
    {
        return "users/{$uid}";
    }

    private function vaultItemsCollectionPath(string $uid): string
    {
        return "users/{$uid}/vault_items";
    }

    private function vaultItemPath(string $uid, string $itemId): string
    {
        return $this->vaultItemsCollectionPath($uid)."/{$itemId}";
    }

    private function foldersCollectionPath(string $uid): string
    {
        return "users/{$uid}/folders";
    }

    private function folderPath(string $uid, string $folderId): string
    {
        return $this->foldersCollectionPath($uid)."/{$folderId}";
    }

    private function tagsCollectionPath(string $uid): string
    {
        return "users/{$uid}/tags";
    }

    private function tagPath(string $uid, string $tagId): string
    {
        return $this->tagsCollectionPath($uid)."/{$tagId}";
    }
}
