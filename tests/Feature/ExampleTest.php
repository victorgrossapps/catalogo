<?php

namespace Tests\Feature;

// use Illuminate\Foundation\Testing\RefreshDatabase;
use Tests\TestCase;

class ExampleTest extends TestCase
{
    /**
     * A basic test example.
     */
    public function test_the_application_redirects_to_admin_panel(): void
    {
        $response = $this->get('/');

        $response->assertRedirect('/admin');
    }

    public function test_admin_redirects_guest_to_login(): void
    {
        $response = $this->get('/admin');

        $response->assertRedirect('/login');
        $this->get('/login')->assertRedirect('/admin/login');
    }
}
