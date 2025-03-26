FROM mysql:8.0.23

ENV MYSQL_ROOT_PASSWORD= \
    TZ=America/Recife

CMD ["mysqld", "--default-authentication-plugin=mysql_native_password", "--sql-mode="]

EXPOSE 3306

VOLUME ["/var/lib/mysql"]