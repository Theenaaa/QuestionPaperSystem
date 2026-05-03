import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  user = {
    username: '',
    password: ''
  };

  constructor(private auth: AuthService, private router: Router) {}

  login() {
    this.auth.login(this.user).subscribe({
      next: (res: any) => {
        console.log('Login Response:', res); // 👈 Added this so you can see exactly what backend sends

        // Safely extract token in case your backend uses a different JSON key
        const actualToken = res.token || res.jwtToken || res.accessToken;
        const actualRole = res.role || 'USER';

        localStorage.setItem('token', actualToken);
        localStorage.setItem('role', actualRole);

        // We are navigating everyone to /dashboard because your RBAC logic (hide/show Add Question) 
        // is already beautifully built into the Dashboard component itself!
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.log(err);
        alert('Login failed');
      }
    });
  }
}
