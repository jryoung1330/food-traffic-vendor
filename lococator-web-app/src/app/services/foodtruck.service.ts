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

  private userData: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  public user$ = this.userData.asObservable();

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
      this.httpClient.get('http://localhost:8888/foodtrucks/city='+city+'/state='+state)
        .subscribe((payload) => {this.foodtruckData.next(payload);});
    }
  }

  public postNewUser(user:User) {
    console.log(JSON.stringify(user));
    console.log(user);
    this.httpClient.post('http://localhost:8888/users', JSON.stringify(user), header)
      .subscribe((payload)=>{this.userData.next(payload);});
  }
}
