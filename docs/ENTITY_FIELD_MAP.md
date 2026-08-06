# Mapa de campos das entidades

Gerado a partir das anotacoes JPA em `src/main/java`. Cada tabela mostra os campos persistidos e suas relacoes; campos herdados aparecem na entidade base `User`.

Legenda: `PK` chave primaria; `gerado` valor gerado pelo JPA; `enum` persistido como texto; `M:1`, `1:N`, `N:N` e `1:1` indicam relacoes.

## Indice

- [Admin](#admin)
- [AuditLog](#auditlog)
- [RefreshToken](#refreshtoken)
- [FirstAccessCode](#firstaccesscode)
- [PasswordResetToken](#passwordresettoken)
- [AutonomousMaintenance](#autonomousmaintenance)
- [Buy](#buy)
- [BuyItem](#buyitem)
- [ClassGroup](#classgroup)
- [Coordinator](#coordinator)
- [Designation](#designation)
- [Equipment](#equipment)
- [Event](#event)
- [HelperMaterial](#helpermaterial)
- [HistoryLog](#historylog)
- [Inconvenience5S](#inconvenience5s)
- [Machine](#machine)
- [MachineLog](#machinelog)
- [MaintenanceRequest](#maintenancerequest)
- [Media](#media)
- [Notification](#notification)
- [Organization](#organization)
- [Place](#place)
- [Student](#student)
- [Teacher](#teacher)
- [User](#user)
- [NotificationPreference](#notificationpreference)
- [UserImport](#userimport)
- [UserImportItem](#userimportitem)

## Admin

Tabela: `admin`. Campos comuns herdados de `User`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |

## AuditLog

Tabela: `audit_log`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `audit_log_id`; obrigatorio; imutavel apos criar |
| `userId` | `UUID` | coluna `user_id` |
| `username` | `String` | coluna `username` |
| `action` | `String` | coluna `action`; obrigatorio |
| `entityType` | `String` | coluna `entity_type` |
| `entityId` | `UUID` | coluna `entity_id` |
| `endpoint` | `String` | coluna `endpoint` |
| `httpMethod` | `String` | coluna `http_method` |
| `ipAddress` | `String` | coluna `ip_address` |
| `userAgent` | `String` | coluna `user_agent` |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `success` | `boolean` | coluna `success`; obrigatorio |
| `details` | `String` | coluna `details` |

## RefreshToken

Tabela: `refresh_token`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `refresh_token_id`; obrigatorio; imutavel apos criar |
| `user` | `User` | M:1 com `User` por `user_id`; obrigatorio; imutavel apos criar |
| `tokenHash` | `String` | coluna `token_hash`; obrigatorio; unico; imutavel apos criar |
| `expiresAt` | `LocalDateTime` | coluna `expires_at`; obrigatorio; imutavel apos criar |
| `revokedAt` | `LocalDateTime` | coluna `revoked_at` |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `ipAddress` | `String` | coluna `ip_address`; imutavel apos criar |
| `userAgent` | `String` | coluna `user_agent`; imutavel apos criar |

## FirstAccessCode

Tabela: `first_access_code`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `first_access_code_id`; obrigatorio; imutavel apos criar |
| `user` | `User` | M:1 com `User` por `user_id`; obrigatorio; imutavel apos criar |
| `codeHash` | `String` | coluna `code_hash`; obrigatorio; imutavel apos criar |
| `attempts` | `int` | coluna `attempts`; obrigatorio |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `expiresAt` | `LocalDateTime` | coluna `expires_at`; obrigatorio; imutavel apos criar |
| `usedAt` | `LocalDateTime` | coluna `used_at` |

## PasswordResetToken

Tabela: `password_reset_token`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `password_reset_token_id`; obrigatorio; imutavel apos criar |
| `user` | `User` | M:1 com `User` por `user_id`; obrigatorio; imutavel apos criar |
| `tokenHash` | `String` | coluna `token_hash`; obrigatorio; unico; imutavel apos criar |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `expiresAt` | `LocalDateTime` | coluna `expires_at`; obrigatorio; imutavel apos criar |
| `usedAt` | `LocalDateTime` | coluna `used_at` |
| `requestedIp` | `String` | coluna `requested_ip`; imutavel apos criar |

## AutonomousMaintenance

Tabela: `autonomous_maintenance`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `autonomous_maintenance_id`; obrigatorio; imutavel apos criar |
| `equipmentSituation` | `EquipmentSituation` | coluna `equipment_situation`; enum como texto; obrigatorio |
| `scheduledFor` | `LocalDateTime` | coluna `scheduled_for`; obrigatorio |
| `inspectedAt` | `LocalDateTime` | coluna `inspected_at` |
| `inspectedMachine` | `Machine` | M:1 com `Machine` por `inspected_machine_id`; obrigatorio |
| `equipmentCondition` | `EquipmentCondition` | coluna `equipment_condition`; enum como texto; obrigatorio |
| `identifiedNonconformities` | `String` | coluna `identified_nonconformities` |
| `responsibleTeacher` | `Teacher` | M:1 com `Teacher` por `responsible_teacher_id`; obrigatorio; imutavel apos criar |
| `assignedStudents` | `List<Student>` | padrao `new ArrayList<>()` |
| `createdBy` | `User` | M:1 com `User` por `created_by_user_id`; obrigatorio; imutavel apos criar |
| `status` | `AutonomousMaintenanceStatus` | coluna `status`; enum como texto; obrigatorio; padrao `AutonomousMaintenanceStatus.PENDENTE_APROVACAO_COORDENADOR` |
| `coordinatorApprover` | `User` | M:1 com `User` por `coordinator_approver_user_id` |
| `approvedAt` | `LocalDateTime` | coluna `approved_at` |
| `rejectionReason` | `String` | coluna `rejection_reason` |
| `calendarEvent` | `Event` | 1:1 com `Event` por `calendar_event_id`; unico |
| `media` | `List<Media>` | padrao `new ArrayList<>()` |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `updatedAt` | `LocalDateTime` | coluna `updated_at`; obrigatorio |

## Buy

Tabela: `buy`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `buy_id`; obrigatorio; imutavel apos criar |
| `status` | `BuyStatus` | coluna `status`; enum como texto; obrigatorio; padrao `BuyStatus.NAO_VISUALIZADO` |
| `createdBy` | `User` | M:1 com `User` por `created_by_user_id`; obrigatorio |
| `notifiedTeacher` | `Teacher` | M:1 com `Teacher` por `notified_teacher_id` |
| `purchaseJustification` | `String` | coluna `purchase_justification` |
| `classGroup` | `ClassGroup` | M:1 com `ClassGroup` por `class_group_id` |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `items` | `List<BuyItem>` | padrao `new ArrayList<>()` |
| `media` | `List<Media>` | padrao `new ArrayList<>()` |

## BuyItem

Tabela: `buy_item`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `buy_item_id`; obrigatorio; imutavel apos criar |
| `buy` | `Buy` | M:1 com `Buy` por `buy_id`; obrigatorio |
| `equipment` | `Equipment` | M:1 com `Equipment` por `equipment_id`; obrigatorio |
| `quantity` | `Integer` | coluna `quantity`; obrigatorio |
| `technicalSpecification` | `String` | coluna `technical_specification` |
| `sap` | `String` | coluna `sap` |
| `patrimony` | `String` | coluna `patrimony` |
| `tag` | `String` | coluna `tag` |
| `mechanicalSet` | `String` | coluna `mechanical_set` |

## ClassGroup

Tabela: `class_group`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `class_group_id`; obrigatorio; imutavel apos criar |
| `acronym` | `String` | coluna `acronym`; obrigatorio; unico |
| `enabled` | `boolean` | coluna `enabled`; obrigatorio; padrao `true` |
| `teachers` | `List<Teacher>` | N:N com `Teacher` por `class_group_id` via `class_group_teacher`; padrao `new ArrayList<>()` |
| `students` | `List<Student>` | N:N com `Student` por `class_group_id` via `class_group_student`; padrao `new ArrayList<>()` |

## Coordinator

Tabela: `coordinator`. Campos comuns herdados de `User`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |

## Designation

Tabela: `designation`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `designation_id`; obrigatorio; imutavel apos criar |
| `sector` | `Sector` | coluna `designation_sector`; enum como texto |

## Equipment

Tabela: `equipment`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `equipment_id`; obrigatorio; imutavel apos criar |
| `name` | `String` | coluna `equipment_name`; obrigatorio |
| `sap` | `String` | coluna `equipment_sap`; unico |
| `patrimony` | `String` | coluna `equipment_patrimony` |
| `tag` | `String` | coluna `equipment_tag` |
| `unitPrice` | `BigDecimal` | coluna `unit_price` |
| `availableQuantity` | `Integer` | coluna `available_quantity`; obrigatorio |
| `media` | `List<Media>` | padrao `new ArrayList<>()` |

## Event

Tabela: `event`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `event_id`; obrigatorio; imutavel apos criar |
| `scheduledAction` | `String` | coluna `scheduled_action`; obrigatorio |
| `criticality` | `TaskCriticality` | coluna `criticality`; enum como texto; obrigatorio |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `scheduledFor` | `LocalDateTime` | coluna `scheduled_for`; obrigatorio |
| `requestedAt` | `LocalDateTime` | coluna `requested_at`; obrigatorio |
| `student` | `Student` | M:1 com `Student` por `student_id` |
| `teacher` | `Teacher` | M:1 com `Teacher` por `teacher_id`; obrigatorio |
| `equipment` | `Equipment` | M:1 com `Equipment` por `equipment_id` |
| `machine` | `Machine` | M:1 com `Machine` por `machine_id`; obrigatorio |
| `place` | `Place` | M:1 com `Place` por `place_id`; obrigatorio |
| `maintenanceType` | `MaintenanceType` | coluna `maintenance_type`; enum como texto; obrigatorio |
| `status` | `TaskSituation` | coluna `status`; enum como texto; obrigatorio; padrao `TaskSituation.PENDENTE` |

## HelperMaterial

Tabela: `helper_material`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `helper_material_id`; obrigatorio; imutavel apos criar |
| `title` | `String` | coluna `title`; obrigatorio |
| `description` | `String` | coluna `description` |
| `url` | `String` | coluna `url`; obrigatorio |
| `type` | `HelperMaterialType` | coluna `type`; enum como texto; obrigatorio |

## HistoryLog

Tabela: `history_log`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `history_log_id`; obrigatorio; imutavel apos criar |
| `action` | `HistoryAction` | coluna `action`; enum como texto; obrigatorio |
| `entityType` | `HistoryEntityType` | coluna `entity_type`; enum como texto; obrigatorio |
| `entityId` | `UUID` | coluna `entity_id`; obrigatorio |
| `description` | `String` | coluna `description` |
| `actorRole` | `Role` | coluna `actor_role`; enum como texto; obrigatorio |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `actor` | `User` | M:1 com `User` por `actor_id`; obrigatorio |

## Inconvenience5S

Tabela: `inconvenience_5s`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `inconvenience_5s_id`; obrigatorio; imutavel apos criar |
| `inconvenience` | `String` | coluna `inconvenience`; obrigatorio |
| `status` | `Inconvenience5SStatus` | coluna `status`; enum como texto; obrigatorio; padrao `Inconvenience5SStatus.NAO_VISUALIZADA` |
| `place` | `Place` | M:1 com `Place` por `place_id`; obrigatorio |
| `notifiedTeacher` | `Teacher` | M:1 com `Teacher` por `notified_teacher_id`; obrigatorio |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `classGroup` | `ClassGroup` | M:1 com `ClassGroup` por `class_group_id`; obrigatorio |
| `createdBy` | `User` | M:1 com `User` por `created_by_user_id`; obrigatorio |
| `involvedStudents` | `List<Student>` | padrao `new ArrayList<>()` |
| `description` | `String` | coluna `description` |
| `media` | `List<Media>` | padrao `new ArrayList<>()` |
| `registrationPeriod` | `RegistrationPeriod` | coluna `registration_period`; enum como texto; obrigatorio |

## Machine

Tabela: `machine`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `machine_id`; obrigatorio; imutavel apos criar |
| `name` | `String` | coluna `machine_name`; obrigatorio |
| `patrimony` | `String` | coluna `machine_patrimony`; obrigatorio |
| `condition` | `EquipmentCondition` | coluna `machine_condition`; enum como texto; obrigatorio |
| `tag` | `String` | coluna `machine_tag` |
| `place` | `Place` | M:1 com `Place` por `place_id`; obrigatorio |
| `image` | `String` | coluna `machine_image` |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `machineLogs` | `List<MachineLog>` | 1:N com `MachineLog`; padrao `new ArrayList<>()` |

## MachineLog

Tabela: `machine_log`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `machine_log_id`; obrigatorio; imutavel apos criar |
| `title` | `String` | coluna `title` |
| `description` | `String` | coluna `description` |
| `executionReport` | `String` | coluna `execution_report` |
| `taskSituation` | `TaskSituation` | coluna `task_situation`; enum como texto; obrigatorio |
| `machine` | `Machine` | M:1 com `Machine` por `machine_id`; obrigatorio |
| `servicePerformed` | `String` | coluna `service_performed` |
| `teacherConcludedAt` | `LocalDateTime` | coluna `teacher_concluded_at` |
| `responsibleTeacher` | `Teacher` | M:1 com `Teacher` por `responsible_teacher_id` |
| `registeredAt` | `LocalDateTime` | coluna `registered_at`; obrigatorio; imutavel apos criar |
| `executionStartedAt` | `LocalDateTime` | coluna `execution_started_at` |
| `executionEndedAt` | `LocalDateTime` | coluna `execution_ended_at` |
| `plannedAction` | `String` | coluna `planned_action` |
| `taskCriticality` | `TaskCriticality` | coluna `task_criticality`; enum como texto; obrigatorio |
| `place` | `Place` | M:1 com `Place` por `place_id` |
| `maintenanceType` | `MaintenanceType` | coluna `maintenance_type`; enum como texto |
| `classGroup` | `ClassGroup` | M:1 com `ClassGroup` por `class_group_id` |
| `assignedStudents` | `List<Student>` | padrao `new ArrayList<>()` |
| `media` | `List<Media>` | padrao `new ArrayList<>()` |
| `reportLink` | `String` | coluna `report_link` |
| `createdBy` | `User` | M:1 com `User` por `created_by_user_id`; obrigatorio |

## MaintenanceRequest

Tabela: `maintenance_request`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `maintenance_request_id`; obrigatorio; imutavel apos criar |
| `status` | `MaintenanceRequestStatus` | coluna `status`; enum como texto; obrigatorio; padrao `MaintenanceRequestStatus.PENDENTE_APROVACAO_PROFESSOR` |
| `sector` | `Sector` | coluna `sector`; enum como texto; obrigatorio |
| `priority` | `Priority` | coluna `priority`; enum como texto; obrigatorio |
| `createdBy` | `User` | M:1 com `User` por `created_by_user_id`; obrigatorio |
| `assignedStudents` | `List<Student>` | N:N com `Student` por `maintenance_request_id` via `maintenance_request_student`; padrao `new ArrayList<>()` |
| `place` | `Place` | M:1 com `Place` por `place_id`; obrigatorio |
| `description` | `String` | coluna `description`; obrigatorio |
| `media` | `List<Media>` | 1:N com `Media` por `maintenance_request_id` via `maintenance_request_media`; padrao `new ArrayList<>()` |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `notifiedTeacher` | `Teacher` | M:1 com `Teacher` por `notified_teacher_id`; obrigatorio |
| `machine` | `Machine` | M:1 com `Machine` por `machine_id`; obrigatorio |
| `approvedBy` | `User` | M:1 com `User` por `approved_by_user_id` |
| `approvedAt` | `LocalDateTime` | coluna `approved_at` |
| `rejectionReason` | `String` | coluna `rejection_reason` |
| `workOrderNumber` | `String` | coluna `work_order_number`; unico |
| `workOrderCreatedAt` | `LocalDateTime` | coluna `work_order_created_at` |
| `workOrderCreatedBy` | `User` | M:1 com `User` por `work_order_created_by_user_id` |
| `coordinatorApprovedBy` | `User` | M:1 com `User` por `coordinator_approved_by_user_id` |
| `coordinatorApprovedAt` | `LocalDateTime` | coluna `coordinator_approved_at` |
| `coordinatorRejectionReason` | `String` | coluna `coordinator_rejection_reason` |

## Media

Tabela: `media`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `media_id`; obrigatorio; imutavel apos criar |
| `description` | `String` | coluna `media_description` |
| `mediaType` | `MediaType` | coluna `media_type`; enum como texto; obrigatorio |
| `image` | `String` | coluna `storage_key`; obrigatorio |
| `originalName` | `String` | coluna `original_name`; obrigatorio |
| `contentType` | `String` | coluna `content_type`; obrigatorio |
| `fileSize` | `Long` | coluna `file_size`; obrigatorio |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `uploadedBy` | `User` | M:1 com `User` por `uploaded_by`; imutavel apos criar |
| `organization` | `Organization` | M:1 com `Organization` por `organization_id`; imutavel apos criar |
| `active` | `boolean` | coluna `active`; obrigatorio; padrao `true` |

## Notification

Tabela: `convencao JPA`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `notification_id`; obrigatorio; imutavel apos criar |
| `email` | `String` | mapeamento por convencao JPA |
| `title` | `String` | mapeamento por convencao JPA |
| `about` | `String` | mapeamento por convencao JPA |
| `description` | `String` | mapeamento por convencao JPA |
| `statusRead` | `boolean` | padrao `false` |

## Organization

Tabela: `convencao JPA`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `organization_id`; obrigatorio; imutavel apos criar |
| `name` | `String` | coluna `name`; obrigatorio |
| `type` | `OrganizationType` | coluna `type`; enum como texto; obrigatorio |
| `emailDomain` | `String` | coluna `email_domain`; obrigatorio |
| `active` | `boolean` | coluna `active`; obrigatorio; padrao `true` |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `updatedAt` | `LocalDateTime` | coluna `updated_at`; obrigatorio |
| `version` | `Long` | coluna `version`; controle otimista de versao; obrigatorio |

## Place

Tabela: `place`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `place_id`; obrigatorio; imutavel apos criar |
| `name` | `String` | coluna `place_name`; obrigatorio; unico |
| `machines` | `List<Machine>` | 1:N com `Machine`; padrao `new ArrayList<>()` |

## Student

Tabela: `student`. Campos comuns herdados de `User`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `classGroups` | `List<ClassGroup>` | N:N com `ClassGroup`; padrao `new ArrayList<>()` |

## Teacher

Tabela: `teacher`. Campos comuns herdados de `User`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `classGroups` | `List<ClassGroup>` | N:N com `ClassGroup`; padrao `new ArrayList<>()` |

## User

Tabela: `users`. Heranca JPA: `JOINED`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `user_id`; obrigatorio; imutavel apos criar |
| `name` | `String` | coluna `user_name`; obrigatorio |
| `username` | `String` | coluna `username`; obrigatorio; unico |
| `email` | `String` | coluna `user_email`; obrigatorio; unico |
| `password` | `String` | coluna `user_password`; oculto no JSON; obrigatorio |
| `role` | `Role` | coluna `user_role`; enum como texto; obrigatorio |
| `enabled` | `boolean` | coluna `user_enabled`; obrigatorio; padrao `true` |
| `accountNonLocked` | `boolean` | coluna `account_non_locked`; obrigatorio; padrao `true` |
| `organization` | `Organization` | M:1 com `Organization` por `organization_id`; obrigatorio |
| `passwordChangeRequired` | `boolean` | coluna `password_change_required`; obrigatorio |
| `temporaryPasswordExpiresAt` | `LocalDateTime` | coluna `temporary_password_expires_at` |
| `passwordChangedAt` | `LocalDateTime` | coluna `password_changed_at` |
| `failedLoginAttempts` | `int` | coluna `failed_login_attempts`; obrigatorio |
| `lockedUntil` | `LocalDateTime` | coluna `locked_until` |
| `lastFailedLoginAt` | `LocalDateTime` | coluna `last_failed_login_at` |
| `lockoutCount` | `int` | coluna `lockout_count`; obrigatorio |
| `statusChangeReason` | `String` | coluna `status_change_reason` |
| `statusChangedAt` | `LocalDateTime` | coluna `status_changed_at` |
| `statusChangedBy` | `UUID` | coluna `status_changed_by` |
| `securityVersion` | `long` | coluna `security_version`; obrigatorio |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `updatedAt` | `LocalDateTime` | coluna `updated_at`; obrigatorio |
| `numberCard` | `String` | coluna `number_card`; obrigatorio; unico; padrao `UUID.randomUUID().toString()` |
| `version` | `Long` | coluna `version`; controle otimista de versao; obrigatorio |

## NotificationPreference

Tabela: `notification_preference`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `notification_preference_id`; obrigatorio; imutavel apos criar |
| `user` | `User` | 1:1 com `User` por `user_id`; obrigatorio; unico; imutavel apos criar |
| `emailEnabled` | `boolean` | coluna `email_enabled`; obrigatorio; padrao `true` |
| `inAppEnabled` | `boolean` | coluna `in_app_enabled`; obrigatorio; padrao `true` |
| `occurrenceNotifications` | `boolean` | coluna `occurrence_notifications`; obrigatorio; padrao `true` |
| `purchaseNotifications` | `boolean` | coluna `purchase_notifications`; obrigatorio; padrao `true` |
| `inspectionNotifications` | `boolean` | coluna `inspection_notifications`; obrigatorio; padrao `true` |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `updatedAt` | `LocalDateTime` | coluna `updated_at`; obrigatorio |
| `version` | `Long` | coluna `version`; controle otimista de versao; obrigatorio |

## UserImport

Tabela: `user_import`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `user_import_id`; obrigatorio; imutavel apos criar |
| `filename` | `String` | coluna `filename`; obrigatorio; imutavel apos criar |
| `importedBy` | `User` | M:1 com `User` por `imported_by`; obrigatorio; imutavel apos criar |
| `organization` | `Organization` | M:1 com `Organization` por `organization_id`; imutavel apos criar |
| `status` | `UserImportStatus` | coluna `status`; enum como texto; obrigatorio |
| `totalRows` | `int` | coluna `total_rows`; obrigatorio |
| `createdCount` | `int` | coluna `created_count`; obrigatorio |
| `failedCount` | `int` | coluna `failed_count`; obrigatorio |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |
| `completedAt` | `LocalDateTime` | coluna `completed_at` |

## UserImportItem

Tabela: `user_import_item`.

| Campo | Tipo Java | Persistencia e relacao |
| --- | --- | --- |
| `id` | `UUID` | PK; gerado; coluna `user_import_item_id`; obrigatorio; imutavel apos criar |
| `userImport` | `UserImport` | M:1 com `UserImport` por `user_import_id`; obrigatorio; imutavel apos criar |
| `rowNumber` | `int` | coluna `row_number`; obrigatorio; imutavel apos criar |
| `name` | `String` | coluna `name`; imutavel apos criar |
| `username` | `String` | coluna `username`; imutavel apos criar |
| `email` | `String` | coluna `email`; imutavel apos criar |
| `role` | `Role` | coluna `role`; enum como texto; imutavel apos criar |
| `organizationValue` | `String` | coluna `organization_value`; imutavel apos criar |
| `status` | `UserImportItemStatus` | coluna `status`; enum como texto; obrigatorio |
| `createdUser` | `User` | M:1 com `User` por `created_user_id`; imutavel apos criar |
| `errorCode` | `String` | coluna `error_code`; imutavel apos criar |
| `errorField` | `String` | coluna `error_field`; imutavel apos criar |
| `errorMessage` | `String` | coluna `error_message`; imutavel apos criar |
| `createdAt` | `LocalDateTime` | coluna `created_at`; obrigatorio; imutavel apos criar |


## Relacoes diretas

| Entidade | Campo | Destino | Cardinalidade |
| --- | --- | --- | --- |
| `RefreshToken` | `user` | `User` | M:1 |
| `FirstAccessCode` | `user` | `User` | M:1 |
| `PasswordResetToken` | `user` | `User` | M:1 |
| `AutonomousMaintenance` | `inspectedMachine` | `Machine` | M:1 |
| `AutonomousMaintenance` | `responsibleTeacher` | `Teacher` | M:1 |
| `AutonomousMaintenance` | `assignedStudents` | `Student` | N:N |
| `AutonomousMaintenance` | `createdBy` | `User` | M:1 |
| `AutonomousMaintenance` | `coordinatorApprover` | `User` | M:1 |
| `AutonomousMaintenance` | `calendarEvent` | `Event` | 1:1 |
| `AutonomousMaintenance` | `media` | `Media` | 1:N |
| `Buy` | `createdBy` | `User` | M:1 |
| `Buy` | `notifiedTeacher` | `Teacher` | M:1 |
| `Buy` | `classGroup` | `ClassGroup` | M:1 |
| `Buy` | `items` | `BuyItem` | 1:N |
| `Buy` | `media` | `Media` | 1:N |
| `BuyItem` | `buy` | `Buy` | M:1 |
| `BuyItem` | `equipment` | `Equipment` | M:1 |
| `ClassGroup` | `teachers` | `Teacher` | N:N |
| `ClassGroup` | `students` | `Student` | N:N |
| `Equipment` | `media` | `Media` | 1:N |
| `Event` | `student` | `Student` | M:1 |
| `Event` | `teacher` | `Teacher` | M:1 |
| `Event` | `equipment` | `Equipment` | M:1 |
| `Event` | `machine` | `Machine` | M:1 |
| `Event` | `place` | `Place` | M:1 |
| `HistoryLog` | `actor` | `User` | M:1 |
| `Inconvenience5S` | `place` | `Place` | M:1 |
| `Inconvenience5S` | `notifiedTeacher` | `Teacher` | M:1 |
| `Inconvenience5S` | `classGroup` | `ClassGroup` | M:1 |
| `Inconvenience5S` | `createdBy` | `User` | M:1 |
| `Inconvenience5S` | `involvedStudents` | `Student` | N:N |
| `Inconvenience5S` | `media` | `Media` | 1:N |
| `Machine` | `place` | `Place` | M:1 |
| `Machine` | `machineLogs` | `MachineLog` | 1:N |
| `MachineLog` | `machine` | `Machine` | M:1 |
| `MachineLog` | `responsibleTeacher` | `Teacher` | M:1 |
| `MachineLog` | `place` | `Place` | M:1 |
| `MachineLog` | `classGroup` | `ClassGroup` | M:1 |
| `MachineLog` | `assignedStudents` | `Student` | N:N |
| `MachineLog` | `media` | `Media` | 1:N |
| `MachineLog` | `createdBy` | `User` | M:1 |
| `MaintenanceRequest` | `createdBy` | `User` | M:1 |
| `MaintenanceRequest` | `assignedStudents` | `Student` | N:N |
| `MaintenanceRequest` | `place` | `Place` | M:1 |
| `MaintenanceRequest` | `media` | `Media` | 1:N |
| `MaintenanceRequest` | `notifiedTeacher` | `Teacher` | M:1 |
| `MaintenanceRequest` | `machine` | `Machine` | M:1 |
| `MaintenanceRequest` | `approvedBy` | `User` | M:1 |
| `MaintenanceRequest` | `workOrderCreatedBy` | `User` | M:1 |
| `MaintenanceRequest` | `coordinatorApprovedBy` | `User` | M:1 |
| `Media` | `uploadedBy` | `User` | M:1 |
| `Media` | `organization` | `Organization` | M:1 |
| `Place` | `machines` | `Machine` | 1:N |
| `Student` | `classGroups` | `ClassGroup` | N:N |
| `Teacher` | `classGroups` | `ClassGroup` | N:N |
| `User` | `organization` | `Organization` | M:1 |
| `NotificationPreference` | `user` | `User` | 1:1 |
| `UserImport` | `importedBy` | `User` | M:1 |
| `UserImport` | `organization` | `Organization` | M:1 |
| `UserImportItem` | `userImport` | `UserImport` | M:1 |
| `UserImportItem` | `createdUser` | `User` | M:1 |
