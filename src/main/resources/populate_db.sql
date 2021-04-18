--USERS
insert into users values (
        1,
       'Андрей',
       'Петров',
       'petrov',
       '$2a$08$OTxvfaRgR.9icLdGL0BiKuZQnGL4STdTLtowmyBsBIFEZFtia0I6W',
       'STUDENT');
insert into users values (
        2,
       'Ларина',
       'Анна',
       'larina',
       '$2a$08$OTxvfaRgR.9icLdGL0BiKuZQnGL4STdTLtowmyBsBIFEZFtia0I6W',
       'STUDENT');
insert into users values (
       3,
       'Иван',
       'Иванов',
       'ivanov',
       '$2a$08$OTxvfaRgR.9icLdGL0BiKuZQnGL4STdTLtowmyBsBIFEZFtia0I6W',
       'TEACHER');
insert into users values (
       4,
       'Никита',
       'Савин',
       'savin',
       '$2a$08$OTxvfaRgR.9icLdGL0BiKuZQnGL4STdTLtowmyBsBIFEZFtia0I6W',
       'TEACHER');
insert into users values (
       5,
       'Антон',
       'Орлов',
       'orlov',
       '$2a$08$OTxvfaRgR.9icLdGL0BiKuZQnGL4STdTLtowmyBsBIFEZFtia0I6W',
       'ADMIN');

--DB_INFO
insert into DB_INFO values
(
    1,
    'university',
    null,
    'src\\main\\resources\\excel\\1.xlsx',
    3
);
insert into DB_INFO values
(
    2,
    'school',
    null,
    'src\\main\\resources\\excel\\2.xlsx',
    4
);

--COURSES
insert into COURSES values
(
    1,
    'Основы SQL для мадших курсов',
    'Курс включает в себя тесты на знание простейших SELECT запросов.',
    3
);
insert into COURSES values
(
    2,
    'Практика SQL',
    '',
    3
);
insert into COURSES values
(
    3,
    'Практика SQL для курса "Введение в "SQL"',
    '',
    4
);
insert into COURSES values
(
    4,
    'Тесты по SQL для занятий группы Иванова',
    'Тесты для проведения практических занятий',
    3
);
insert into COURSES values
(
    5,
    'Практика SQL повышенной сложности для курса "Введение в "SQL"',
    '',
    4
);
insert into COURSES values
(
    6,
    'Тесты по SQL для занятий группы Петрова',
    '',
    4
);

insert into TESTS values
(
    1,
    'Выборка данных (SELECT)',
    1,
    3,
    1,
    1
);
insert into TESTS values
(
    2,
    'Сортировка (ORDER BY)',
    2,
    3,
    1,
    1
);
insert into TESTS values
(
    3,
    'Фильтрация данных (WHERE)',
    3,
    3,
    1,
    1
);
insert into TESTS values
(
    4,
    'Символы подстановки и регулярные выражения (LIKE)',
    4,
    3,
    1,
    1
);
insert into TESTS values
(
    5,
    'Группировка данных (GROUP BY)',
    5,
    3,
    1,
    1
);
insert into TESTS values
(
    6,
    'Объединение таблиц (INNER JOIN)',
    6,
    3,
    1,
    1
);
insert into TESTS values
(
    7,
    'Расширенное объединение таблиц (OUTER JOIN)',
    7,
    3,
    1,
    1
);
insert into TESTS values
(
    8,
    'Комбинированные запросы (UNION)',
    8,
    3,
    1,
    1
);

--QUESTIONS
insert into QUESTIONS values
(
    1,
    'Выберете все записи из таблицы SUBJECT',
    1
);
insert into ANSWERS values
(
    1,
    'SELECT * FROM SUBJECT;',
    null,
    1, --quest id
    3, --author id
    null --attempt id
);
insert into QUESTIONS values
(
    2,
    'Выберете все записи из таблицы UNIVERSITY',
    1
);
insert into ANSWERS values
(
    2,
    'SELECT * FROM UNIVERSITY;',
    null,
    2, --quest id
    3, --author id
    null --attempt id
);
insert into QUESTIONS values
(
    3,
    'Выберете все записи из таблицы LECTURER',
    1
);
insert into ANSWERS values
(
    3,
    'SELECT * FROM LECTURER;',
    null,
    3, --quest id
    3, --author id
    null --attempt id
);
insert into QUESTIONS values
(
    4,
    'Выберете все записи из таблицы LECTURER отсортированные по полю NAME по убыванию',
    1
);
insert into ANSWERS values
(
    4,
    'SELECT * FROM LECTURER ORDER BY NAME DECS;',
    null,
    4, --quest id
    3, --author id
    null --attempt id
);
insert into QUESTIONS values
(
    5,
    'Напишите запрос для выбора всех записей таблицы SUBJECT отсортированных по семестру и часам',
    1
);
insert into ANSWERS values
(
    5,
    'SELECT * FROM SUBJECT ORDER BY SEMESTER, HOUR;',
    null,
    5, --quest id
    3, --author id
    null --attempt id
);
-- insert into QUESTIONS values
-- (
--     6,
--     '',
--     1
-- );
-- insert into QUESTIONS values
-- (
--     7,
--     '',
--     1
-- );
-- insert into QUESTIONS values
-- (
--     8,
--     '',
--     1
-- );