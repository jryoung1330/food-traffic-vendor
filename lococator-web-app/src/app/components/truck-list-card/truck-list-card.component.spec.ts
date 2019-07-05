import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TruckListCardComponent } from './truck-list-card.component';

describe('TruckListCardComponent', () => {
  let component: TruckListCardComponent;
  let fixture: ComponentFixture<TruckListCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TruckListCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TruckListCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
