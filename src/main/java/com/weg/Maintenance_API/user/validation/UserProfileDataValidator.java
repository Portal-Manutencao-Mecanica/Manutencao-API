package com.weg.Maintenance_API.user.validation;

import com.weg.Maintenance_API.user.dto.request.CreateUserRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UserProfileDataValidator
        implements ConstraintValidator<ValidUserProfileData, CreateUserRequest> {

    @Override
    public boolean isValid(CreateUserRequest request, ConstraintValidatorContext context) {
        if (request == null || request.role() == null) {
            return true;
        }

        return switch (request.role()) {
            case ALUNO -> validForStudent(request, context);
            case PROFESSOR -> validForTeacher(request, context);
            case COORDENADOR -> validWithoutSpecificProfileData(request, context, "COORDENADOR");
            case ADMIN -> validForAdmin(request, context);
        };
    }

    private boolean validForStudent(
            CreateUserRequest request,
            ConstraintValidatorContext context
    ) {
        return request.studentData() != null
                && request.teacherData() == null
                || invalid(
                        context,
                        "studentData",
                        "A role ALUNO exige studentData e nao aceita dados de outro perfil."
                );
    }

    private boolean validForTeacher(
            CreateUserRequest request,
            ConstraintValidatorContext context
    ) {
        return request.studentData() == null
                && request.teacherData() != null
                || invalid(
                        context,
                        "teacherData",
                        "A role PROFESSOR exige teacherData e nao aceita dados de outro perfil."
                );
    }

    private boolean validWithoutSpecificProfileData(
            CreateUserRequest request,
            ConstraintValidatorContext context,
            String role
    ) {
        return request.studentData() == null
                && request.teacherData() == null
                || invalid(
                        context,
                        "role",
                        "A role " + role + " nao aceita dados especificos de perfil."
                );
    }

    private boolean validForAdmin(
            CreateUserRequest request,
            ConstraintValidatorContext context
    ) {
        return request.studentData() == null
                && request.teacherData() == null
                || invalid(
                        context,
                        "role",
                        "A role ADMIN nao aceita dados especificos de perfil."
                );
    }

    private boolean invalid(
            ConstraintValidatorContext context,
            String field,
            String message
    ) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addPropertyNode(field)
                .addConstraintViolation();
        return false;
    }
}
