import { FoodTruck } from "./foodtruck";

export class User {
    id: number;
    email: string;
    username: string;
    passwordHash: string;
    joinDate: Date;
    employeeId: number;
    isOwner: boolean;
    hasAdminPrivileges: boolean;
    foodtruckId: number;
    favorites: Array<FoodTruck>;
}