import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RoutingService {

  constructor(private router: Router) { }

  setActiveIcon() {
      if (this.router.url === '/user'){
        document.getElementById('profile').classList.add('active');
        document.getElementById('home').classList.remove('active');
        document.getElementById('favorites').classList.remove('active');
        document.getElementById('register').classList.remove('active');
      } else if (this.router.url === '/home'){
        document.getElementById('home').classList.add('active');
        document.getElementById('profile').classList.remove('active');
        document.getElementById('favorites').classList.remove('active');
        document.getElementById('register').classList.remove('active');
      } else if (this.router.url === '/favorites') {
        document.getElementById('favorites').classList.add('active');
        document.getElementById('home').classList.remove('active');
        document.getElementById('profile').classList.remove('active');
        document.getElementById('register').classList.remove('active');
      } else if (this.router.url === '/register') {
        document.getElementById('register').classList.add('active');
        document.getElementById('home').classList.remove('active');
        document.getElementById('favorites').classList.remove('active');
        document.getElementById('profile').classList.remove('active');
    } 
  }
}
