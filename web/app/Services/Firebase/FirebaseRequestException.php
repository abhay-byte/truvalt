<?php

namespace App\Services\Firebase;

use RuntimeException;

class FirebaseRequestException extends RuntimeException
{
    public function __construct(
        string $message,
        private readonly int $status = 422,
        private readonly array $details = [],
    ) {
        parent::__construct($message, $status);
    }

    public function status(): int
    {
        return $this->status;
    }

    public function details(): array
    {
        return $this->details;
    }
}
