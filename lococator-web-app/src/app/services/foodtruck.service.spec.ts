import { TestBed } from '@angular/core/testing';

import { FoodtruckService } from './foodtruck.service';

describe('FoodtruckService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: FoodtruckService = TestBed.get(FoodtruckService);
    expect(service).toBeTruthy();
  });
});
