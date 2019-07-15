import { Component, OnInit, Input } from '@angular/core';
import { User } from 'src/entity/user';
import { UserService } from 'src/app/services/user.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  @Input('user') user: User = new User;
  loggedInUser: User;

  constructor(private userService: UserService, private router: Router) { }

  ngOnInit() {
    this.userService.user$.subscribe((data) => {
      if(data != undefined && data != null) {
        this.loggedInUser = data;
        if(this.loggedInUser.id > 0) this.router.navigateByUrl('/home');
      }
    });
  }

  loginUser() {
    this.user.passwordHash = btoa(this.user.passwordHash);
    this.userService.loginUser(this.user);
  }

}
