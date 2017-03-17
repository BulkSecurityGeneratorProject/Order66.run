
const enum StatusEnum {
    'Unknown','Success','Running','SoftFail','HardFail','ForceSuccess'
};
import { Rule } from '../rule';
import { User } from '../../shared';
export class RuleReport {
    constructor(
        public id?: number,
        public reportDate?: any,
        public status?: StatusEnum,
        public log?: any,
        public submitAt?: any,
        public updatedAt?: any,
        public finishAt?: any,
        public rule?: Rule,
        public user?: User,
    ) {
    }
}
