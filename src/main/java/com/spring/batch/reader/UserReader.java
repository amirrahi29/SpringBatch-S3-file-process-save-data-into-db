package com.spring.batch.reader;

import com.spring.batch.model.UserModel;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.S3Object;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserReader {

    @Autowired
    private S3Client s3Client;
    @Value("${spring.s3.bucket}")
    private String bucketName;

    @Bean
    public MultiResourceItemReader<UserModel> reader() {
        List<Resource> resourceList = new ArrayList<>();

        // S3 bucket & prefix
        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix("user") // read all files starting with 'user'
                .build();

        for (S3Object obj : s3Client.listObjectsV2(listReq).contents()) {
            if (obj.key().endsWith(".csv")) {
                InputStream stream = s3Client.getObject(GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(obj.key())
                        .build());
                resourceList.add(new InputStreamResource(stream));
            }
        }

        // File reader
        FlatFileItemReader<UserModel> delegate = new FlatFileItemReader<>();
        delegate.setLinesToSkip(1); // Skip CSV header

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(",");
        tokenizer.setNames("id", "name", "email");

        BeanWrapperFieldSetMapper<UserModel> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(UserModel.class);

        DefaultLineMapper<UserModel> lineMapper = new DefaultLineMapper<>();
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(mapper);

        delegate.setLineMapper(lineMapper);

        // Multi-file reader
        MultiResourceItemReader<UserModel> multiReader = new MultiResourceItemReader<>();
        multiReader.setResources(resourceList.toArray(new Resource[0]));
        multiReader.setDelegate(delegate);
        return multiReader;
    }
}
