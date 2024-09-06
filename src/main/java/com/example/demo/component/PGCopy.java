package com.example.demo.component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PGCopy {
	
	private final DataSource dataSource;
	
	public long copyOut(String tableName, Path path) throws IOException, SQLException {
		Connection connection = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			try(final BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
				return connection.unwrap(org.postgresql.PGConnection.class)
						.getCopyAPI()
						.copyOut(
								String.format("COPY %s TO STDOUT WITH (FORMAT TEXT, HEADER FALSE, ENCODING 'UTF-8')", tableName)
								, writer);
			}
		} finally {
			DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}

	public long copyIn(String tableName, Path path) throws IOException, SQLException {
		Connection connection = null;
		try {
			connection = DataSourceUtils.getConnection(dataSource);
			try(final BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
				return connection.unwrap(org.postgresql.PGConnection.class)
						.getCopyAPI()
						.copyIn(
								String.format("COPY %s FROM STDIN WITH (FORMAT TEXT, HEADER FALSE, ENCODING 'UTF-8')", tableName)
								, reader);
			}
		} finally {
			DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}
	
	public long copyTo(PGCopy pgCopy, String tableName, Path path) throws IOException, SQLException {
		this.copyOut(tableName, path);
		return pgCopy.copyIn(tableName, path);
	}

}
