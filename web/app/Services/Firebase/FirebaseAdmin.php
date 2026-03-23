<?php

namespace App\Services\Firebase;

use Kreait\Firebase\Contract\Auth;
use Kreait\Firebase\Factory;

class FirebaseAdmin
{
    private ?Auth $auth = null;

    public function __construct(
        private readonly FirebaseProjectConfig $config,
    ) {
    }

    public function auth(): Auth
    {
        if ($this->auth !== null) {
            return $this->auth;
        }

        $factory = (new Factory())
            ->withServiceAccount($this->config->serviceAccount())
            ->withProjectId($this->config->projectId());

        return $this->auth = $factory->createAuth();
    }

    public function projectId(): string
    {
        return $this->config->projectId();
    }
}
