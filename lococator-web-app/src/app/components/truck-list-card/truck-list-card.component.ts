import { Component, OnInit, Input } from '@angular/core';
import { FoodTruck } from 'src/entity/foodtruck';

@Component({
  selector: 'app-truck-list-card',
  templateUrl: './truck-list-card.component.html',
  styleUrls: ['./truck-list-card.component.css']
})
export class TruckListCardComponent implements OnInit {

  @Input('foodtruck') foodtruck: FoodTruck;
  expanded: boolean;
  isFavorited: boolean;

  constructor() { }

  ngOnInit() {
  }

  
  toggleExpansion() {
    this.expanded = !this.expanded;
  }

  toggleFavorite() {
    this.isFavorited = !this.isFavorited;
  }


}
