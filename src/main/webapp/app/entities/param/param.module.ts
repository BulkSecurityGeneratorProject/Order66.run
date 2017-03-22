import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ConfigurationManagerReportingSharedModule } from '../../shared';

import {
    ParamService,
    ParamPopupService,
    ParamComponent,
    ParamDetailComponent,
    ParamDialogComponent,
    ParamPopupComponent,
    ParamDeletePopupComponent,
    ParamDeleteDialogComponent,
    paramRoute,
    paramPopupRoute,
    ParamResolvePagingParams,
} from './';

let ENTITY_STATES = [
    ...paramRoute,
    ...paramPopupRoute,
];

@NgModule({
    imports: [
        ConfigurationManagerReportingSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        ParamComponent,
        ParamDetailComponent,
        ParamDialogComponent,
        ParamDeleteDialogComponent,
        ParamPopupComponent,
        ParamDeletePopupComponent,
    ],
    entryComponents: [
        ParamComponent,
        ParamDialogComponent,
        ParamPopupComponent,
        ParamDeleteDialogComponent,
        ParamDeletePopupComponent,
    ],
    providers: [
        ParamService,
        ParamPopupService,
        ParamResolvePagingParams,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ConfigurationManagerReportingParamModule {}
