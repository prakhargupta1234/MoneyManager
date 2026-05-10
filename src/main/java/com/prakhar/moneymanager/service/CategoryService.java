package com.prakhar.moneymanager.service;

import com.prakhar.moneymanager.dto.CategoryDTO;
import com.prakhar.moneymanager.entity.CategoryEntity;
import com.prakhar.moneymanager.entity.ProfileEntity;
import com.prakhar.moneymanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private  final ProfileService profileService;
    private final  CategoryRepository categoryRepository;


    //save category
    public CategoryDTO saveCategory(CategoryDTO categoryDTO){

        // ✅ validation (null + empty + spaces)
        if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name is required");
        }

        ProfileEntity profile = profileService.getCurrentProfile();

        // ✅ normalize (duplicate avoid karne ke liye)
        String name = categoryDTO.getName().trim().toLowerCase();

        // ✅ duplicate check (proper exception)
        if (categoryRepository.existsByNameAndProfileId(name, profile.getId())){
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,   // 🔥 409
                    "Category already exists"
            );
        }

        // ✅ entity mapping
        CategoryEntity categoryEntity = toEntity(categoryDTO, profile);

        // 🔥 IMPORTANT: correct name set karo
        categoryEntity.setName(name);

        // ✅ save
        categoryEntity = categoryRepository.save(categoryEntity);

        return toDTO(categoryEntity);
    }

    //get category for current user
    public List<CategoryDTO> getCategoriesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntities = categoryRepository.findByProfileId(profile.getId());
        return categoryEntities.stream().map(this::toDTO).toList();
    }




    //get category by type for current user
    public  List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> entity= categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return entity.stream().map(this::toDTO).toList();
    }


    //update the name and type of category for current user
//    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO){
//
//        ProfileEntity profile = profileService.getCurrentProfile();
//
//        CategoryEntity categoryEntity = categoryRepository
//                .findByIdAndProfileId(categoryId, profile.getId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
//
//        // validation (optional but recommended)
//        if (categoryDTO.getName() == null || categoryDTO.getName().trim().isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category name is required");
//        }
//
//        // update fields
//        categoryEntity.setName(categoryDTO.getName());
//        categoryEntity.setIcon(categoryDTO.getIcon());
//        categoryEntity.setType(categoryDTO.getType());   // ✅ YE MISS THA
//
//        categoryEntity = categoryRepository.save(categoryEntity);
//
//        return toDTO(categoryEntity);
//    }


    //update category for current user(update only name )
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO){
        ProfileEntity profile = profileService.getCurrentProfile();

        CategoryEntity categoryEntity = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        categoryEntity.setName(categoryDTO.getName());
        categoryEntity.setIcon(categoryDTO.getIcon());
        categoryEntity= categoryRepository.save(categoryEntity);
        return  toDTO(categoryEntity);
    }

    //helper method
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile){
        return CategoryEntity.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .profile(profile)
                .type(categoryDTO.getType())
                .build();
    }

    private CategoryDTO toDTO(CategoryEntity entity){
        return CategoryDTO.builder()
                .id(entity.getId())
                .profileId(entity.getProfile()!= null ? entity.getProfile().getId() : null)
                .name(entity.getName())
                .icon(entity.getIcon())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .type(entity.getType())
                .build();
    }


}
