insert into class_group (acronym)
select seed.acronym
from (values
    ('MEC-2026-01'),
    ('MEC-2026-02'),
    ('MEC-2026-03'),
    ('MEC-2026-04'),
    ('ELE-2026-01'),
    ('ELE-2026-02'),
    ('AUT-2026-01'),
    ('AUT-2026-02'),
    ('MAN-2026-01'),
    ('MAN-2026-02'),
    ('PCM-2026-01'),
    ('PCM-2026-02'),
    ('TURMA-A'),
    ('TURMA-B'),
    ('TURMA-C')
) as seed(acronym)
where not exists (
    select 1
    from class_group existing
    where existing.acronym = seed.acronym
);