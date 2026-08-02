package com.example.cscb07project.interfaces;

import com.example.cscb07project.entities.Carousel;
import com.example.cscb07project.entities.Collection;
import com.google.android.gms.tasks.Task;

import java.util.List;

public interface CarouselInterface {

    Task<Carousel> getCarouselById(String carouselId);

    Task<Void> addArtifactToCarousel(String lotNumber, Carousel carousel);

    Task<Void> deleteCarousel(String carouselId);
}
