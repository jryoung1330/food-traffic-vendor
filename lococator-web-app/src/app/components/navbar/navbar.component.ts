import { Component, OnInit, Input } from '@angular/core';
import { RoutingService } from 'src/app/services/routing.service';
import { User } from 'src/entity/user';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

  @Input('user') user: User = new User;

  constructor(private routingService: RoutingService, private userService: UserService) { }

  ngOnInit() {
    this.userService.user$.subscribe((data) => {
      if(data != undefined && data != null) {
        this.user = data;
      }
    });
  }

  
}
