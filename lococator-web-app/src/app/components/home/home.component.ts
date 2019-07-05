import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FoodTruck } from 'src/entity/foodtruck';
import { RoutingService } from 'src/app/services/routing.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  city: string;
  state: string;
  foodtrucks: Array<FoodTruck>;
  expanded: number;

  constructor(private http: HttpClient, private routingService: RoutingService) { }

  ngOnInit() {
    this.getLocation();
    this.routingService.setActiveIcon();
  }
  
  getLocation() {
    this.http.get('http://ip-api.com/json')
    .subscribe((data) => {
      this.city = data['city'],
      this.state = data['region']
      this.getFoodTrucksInArea();
    });
  }

  getFoodTrucksInArea() {
    if(this.city != null && this.state != null) {
      this.http.get('http://localhost:8888/foodtrucks/city='+this.city+'/state='+this.state)
      .subscribe((data: Array<FoodTruck>) => {
        this.foodtrucks = data;
        console.log(this.foodtrucks);
      });
    }
  }
}
