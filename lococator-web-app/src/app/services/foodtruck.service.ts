import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { User } from 'src/entity/user';

const header = {
  headers: new HttpHeaders({
    //'responseType':'text/plain',
    'Content-Type':'application/json',
    'observe':'response'
  })
};

@Injectable({
  providedIn: 'root'
})

export class HttpService {

  private locationData: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  public location$ = this.locationData.asObservable();

  private foodtruckData: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  public foodtruck$ = this.foodtruckData.asObservable();

  constructor(private httpClient:HttpClient) { }

  public fetchLocation() {
    this.httpClient.get('http://ip-api.com/json')
      .subscribe((payload) => {
        this.locationData.next(payload);
        this.fetchFoodTrucksByLocation(payload['city'], payload['region']);
      });
  }

  public fetchFoodTrucksByLocation(city:string, state:string) {
    if(city != null && state != null) {
      this.httpClient.get('http://35.227.104.10:8888/foodtrucks/city='+city+'/state='+state)
        .subscribe((payload) => {this.foodtruckData.next(payload);});
    }
  }
}
