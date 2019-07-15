import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { User } from 'src/entity/user';
import { BehaviorSubject } from 'rxjs';

const header = {
  headers: new HttpHeaders({
    'Content-Type':'application/json',
    'observe':'response'
  })
};

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userData: BehaviorSubject<any> = new BehaviorSubject<any>([]);
  public user$ = this.userData.asObservable();

  constructor(private httpClient:HttpClient) { }

  public loginUser(user: User) {
    this.httpClient.post('http://35.227.104.10:8888/users/login', JSON.stringify(user), header)
      .subscribe((payload)=>{this.userData.next(payload);})
  }

  public postNewUser(user:User) {
    this.httpClient.post('http://35.227.104.10:8888/users', JSON.stringify(user), header)
      .subscribe((payload)=>{this.userData.next(payload);});
  }
}
