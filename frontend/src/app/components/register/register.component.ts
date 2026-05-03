import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {

  user = {
    username: '',
    password: '',
    role: 'USER'
  };
  
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private authService: AuthService, private router: Router) {}

  register() {
    this.errorMessage = '';
    this.successMessage = '';
    
    if (!this.user.username || !this.user.password || !this.user.role) {
      this.errorMessage = 'Please fill out all fields!';
      return;
    }

    this.authService.register(this.user).subscribe({
      next: () => {
        this.successMessage = "Registered successfully! Redirecting to login...";
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 1500);
      },
      error: (err) => {
        console.error(err);
        this.errorMessage = "Registration failed! Username may already exist.";
      }
    });
  }
}
