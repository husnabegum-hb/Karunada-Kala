package com.example.karunada_kala.data

import com.example.karunada_kala.domain.model.*

object MockData {

   val mockArtForms = listOf(
        ArtForm(
            id = "af1",
            title = "Yakshagana",
            history = "Yakshagana is a traditional theater form that combines dance, music, dialogue, costume, make-up, and stage techniques with a unique style and form. It is developed in Dakshina Kannada, Udupi, Uttara Kannada, Shimoga and western parts of Chikmagalur districts.",
            videoUrl = "https://www.youtube.com/embed/z3pA6p8oU5E"
        ),
        ArtForm(
            id = "af2",
            title = "Bidriware",
            history = "Bidriware is a metal handicraft from Bidar. It was developed in the 14th century C.E. during the rule of the Bahamani Sultans. The term 'Bidriware' originates from the township of Bidar, which is still the chief centre for the manufacture of the unique metalware.",
            videoUrl = "https://www.youtube.com/embed/vO6gN_ZgI2A"
        ),
        ArtForm(
            id = "af3",
            title = "Channapatna Toys",
            history = "Channapatna toys are a particular form of wooden toys (and dolls) that are manufactured in the town of Channapatna in the Ramanagara district of Karnataka state, India. This traditional craft is protected as a geographical indication (GI).",
            videoUrl = "https://www.youtube.com/embed/H_UAnw8pDTo"
        ),
        ArtForm(
            id = "af4",
            title = "Dollu Kunitha",
            history = "Dollu Kunitha is a major popular drum dance of Karnataka, accompanied by singing. It provides both spectacular variety and complexity of skills. Woven around the presiding deity of Beereshwara, it is performed mainly by Kuruba Gowdas.",
            videoUrl = "https://www.youtube.com/embed/PjRk7I3vSbg"
        ),
        ArtForm(
            id = "af5",
            title = "Ilkal Sarees",
            history = "Ilkal sari is a traditional form of sari which is a common feminine wear in India. Ilkal sari takes its name from the town of Ilkal in the Bagalkot district of Karnataka state, India. The weaving of Ilkal sarees is an indoor activity using traditional pit looms.",
            videoUrl = "https://www.youtube.com/embed/AAnV8wXj3iQ"
        ),
        ArtForm(
            id = "af6",
            title = "Kinhal Craft",
            history = "Kinhal Craft or Kinnala Craft is a traditional wooden craft local to the town of Kinhal, or Kinnal, in Koppal District, North Karnataka. The town is famous for Kinhal toys and religious idols.",
            videoUrl = "https://www.youtube.com/embed/p1oY210Y03s"
        ),
        ArtForm(
            id = "af7",
            title = "Mysore Painting",
            history = "Mysore painting is an important form of classical South Indian painting that originated in and around the town of Mysore in Karnataka. These paintings are known for their elegance, muted colours, and attention to detail.",
            videoUrl = "https://www.youtube.com/embed/O8Y66GfGk0E"
        ),
        ArtForm(
            id = "af8",
            title = "Kasuti Embroidery",
            history = "Kasuti is a traditional form of folk embroidery created in Karnataka. Kasuti work which is very intricate sometimes involves putting up to 5,000 stitches by hand and is traditionally made on dress patterns like Ilkal sarees.",
            videoUrl = "https://www.youtube.com/embed/6k4iNl9_qrc"
        ),
        ArtForm(
            id = "af9",
            title = "Sandalwood Carving",
            history = "Sandalwood carving is an ancient craft originating from the Gudigar community in Karnataka. Gudigars are traditional carvers who make exquisite items using fragrant sandalwood, primarily in Shimoga and Uttara Kannada.",
            videoUrl = "https://www.youtube.com/embed/Oia-b-1f8nE"
        ),
        ArtForm(
            id = "af10",
            title = "Togalu Gombeyaata",
            history = "Togalu Gombeyaata is a shadow puppet tradition in Karnataka. The puppets are mostly flat and made from leather. The puppeteers use the puppets to narrate stories from epics like the Ramayana and Mahabharata.",
            videoUrl = "https://www.youtube.com/embed/N-0_8-3Xv3w"
        )
    )

    val mockArtisans = listOf(
        Artisan(
            id = "a1",
            name = "Manjunath Rao",
            type = "Yakshagana Master",
            bio = "Veteran Badagu Thittu artist with 30 years of stage experience.",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQUCXJMGV7lC8Rq4YcKw0FzKbFxbtPd4PvXt-7lc9zhzg&s&ec=121691707?w=800&q=80", // Yakshagana artist / Indian classical performance drama
            phone = "9988776651",
            lat = 13.3409, lng = 74.7421,
            isPerformer = true,
            artFormIds = listOf("af1"),
            studioDescription = "Traditional Yakshagana training center.",
            studioImages = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQUCXJMGV7lC8Rq4YcKw0FzKbFxbtPd4PvXt-7lc9zhzg&s&ec=121691707?w=800") // Performing arts stage
        ),
        Artisan(
            id = "a2",
            name = "Farooq Bidri",
            type = "Bidriware Expert",
            bio = "Specialist in gold and silver inlay on zinc-copper alloys.",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR39tlkVUYz-gPrhiW48QovmcmEOzHviyejuo6sK9GiaQ&s&ec=121691707?w=800&q=80", // Metallic luxury craft / intricate metal engraving
            phone = "9988776652",
            lat = 17.9104, lng = 77.5199,
            isPerformer = false,
            artFormIds = listOf("af2"),
            studioDescription = "Authentic Bidriware workshop in Bidar fort.",
            studioImages = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR39tlkVUYz-gPrhiW48QovmcmEOzHviyejuo6sK9GiaQ&s&ec=121691707?w=800") // Metal forging workshop
        ),
        Artisan(
            id = "a3",
            name = "Kempaiah Toys",
            type = "Channapatna Artist",
            bio = "Master of non-toxic wooden toys using traditional lacquer.",
            imageUrl = "https://s7ap1.scene7.com/is/image/incredibleindia/channapatna-toys-and-dolls-Karnataka-1-craft-hero?qlt=82&ts=1726641410733?w=800&q=80", // Handcrafted colorful wooden toys
            phone = "9988776653",
            lat = 12.6518, lng = 77.2089,
            isPerformer = false,
            artFormIds = listOf("af3"),
            studioDescription = "Eco-friendly wooden toy studio.",
            studioImages = listOf("https://s7ap1.scene7.com/is/image/incredibleindia/channapatna-toys-and-dolls-Karnataka-1-craft-hero?qlt=82&ts=1726641410733?w=800") // Woodcraft lathe workshop
        ),
        Artisan(
            id = "a4",
            name = "Shivappa Dollu",
            type = "Dollu Kunitha Lead",
            bio = "Founder of Janapada Academy, performing drum dances globally.",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSixCtlalJObrpLChtRe8wpe_3YsiYwU0Z5ONyAONabwQ&s&ec=121691707?w=800&q=80", // Traditional Indian folk drummers
            phone = "9988776654",
            lat = 13.9299, lng = 75.5681,
            isPerformer = true,
            artFormIds = listOf("af4"),
            studioDescription = "Community drum dance practice grounds.",
            studioImages = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSixCtlalJObrpLChtRe8wpe_3YsiYwU0Z5ONyAONabwQ&s&ec=121691707?w=800") // Open-air cultural festival area
        ),
        Artisan(
            id = "a5",
            name = "Lakshmi Weaves",
            type = "Ilkal Saree Master",
            bio = "Specializing in Gomi borders and silk Tope Teni pallu.",
            imageUrl = "https://www.verymuchindian.com/cdn/shop/products/DSC_4600.jpg?v=1679120757?w=800&q=80", // Indian traditional silk textile / sarees
            phone = "9988776655",
            lat = 15.9554, lng = 76.1154,
            isPerformer = false,
            artFormIds = listOf("af5"),
            studioDescription = "Traditional pit loom workshop.",
            studioImages = listOf("https://www.verymuchindian.com/cdn/shop/products/DSC_4600.jpg?v=1679120757?w=800") // Traditional weaving loom setup
        ),
        Artisan(
            id = "a6",
            name = "Ravi Kinhal",
            type = "Kinnala Craftman",
            bio = "Crafting religious idols with traditional tamarind seed paste.",
            imageUrl = "https://lh3.googleusercontent.com/ci/AL18g_Ql1aMZlkJUh3xricbcLyBZsLl-PlMCIZURe17eZKLfJ1xA3I-QT_D8jSaUHfSfxXFDMwBkZg=s1200?w=800&q=80", // Traditional Indian clay/wooden religious sculpture
            phone = "9988776656",
            lat = 15.3534, lng = 76.1554,
            isPerformer = false,
            artFormIds = listOf("af6"),
            studioDescription = "Traditional wooden idol studio.",
            studioImages = listOf("https://lh3.googleusercontent.com/ci/AL18g_Ql1aMZlkJUh3xricbcLyBZsLl-PlMCIZURe17eZKLfJ1xA3I-QT_D8jSaUHfSfxXFDMwBkZg=s1200?w=800") // Sculptor art studio tools
        ),
        Artisan(
            id = "a8",
            name = "Savitha Kasuti",
            type = "Kasuti Embroiderer",
            bio = "Preserving the intricate Dharwad thread art heritage.",
            imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdKxurnSBrx7QgmjgkOiSt7DUOIP4Iwsv33Q&s?w=800&q=80", // Intricate geometric hand embroidery / needlework
            phone = "9988776658",
            lat = 15.4589, lng = 75.0078,
            isPerformer = false,
            artFormIds = listOf("af8"),
            studioDescription = "Hand embroidery center for women.",
            studioImages = listOf("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTdKxurnSBrx7QgmjgkOiSt7DUOIP4Iwsv33Q&s?w=800") // Threads and embroidery textile studio
        ),
        Artisan(
            id = "a9",
            name = "Gudigar Carvings",
            type = "Sandalwood Artist",
            bio = "Detailed ivory-finish sandalwood carving specialist.",
            imageUrl = "https://images.unsplash.com/photo-1606293926075-69a00dbfde81?w=800&q=80", // Intricate fragrant wood carving / detail work
            phone = "9988776659",
            lat = 14.3000, lng = 74.8500,
            isPerformer = false,
            artFormIds = listOf("af9"),
            studioDescription = "Aromatic sandalwood workshop.",
            studioImages = listOf("https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=800") // Fine carpentry & woodcarving bench
        )
   
    )

    // Generate 2 posts per artisan
    val mockPosts = mockArtisans.flatMap { artisan ->
        listOf(
            Post(
                id = "${artisan.id}_p1",
                artisanId = artisan.id,
                artFormId = artisan.artFormIds.firstOrNull() ?: "",
                imageUrl = artisan.imageUrl,
                caption = "Proud to share the heritage of ${artisan.type} with the world. #KarunadaKala",
                likesCount = (10..500).random(),
                timestamp = System.currentTimeMillis() - (1000..100000).random()
            ),
            Post(
                id = "${artisan.id}_p2",
                artisanId = artisan.id,
                artFormId = artisan.artFormIds.firstOrNull() ?: "",
                imageUrl = artisan.studioImages.firstOrNull() ?: artisan.imageUrl,
                caption = "A glimpse behind the scenes at our studio today.",
                likesCount = (10..500).random(),
                timestamp = System.currentTimeMillis() - (200000..500000).random()
            )
        )
    }

    val mockEvents = listOf(
        Event(id = "e1", title = "Yakshagana Night", date = "Dec 20, 2025", locationName = "Udupi Sri Krishna Matha", description = "All night performance featuring epic tales.", artisanId = "a1", artFormId = "af1", status = "Upcoming", imageUrl = "https://images.unsplash.com/photo-1582201942988-13e60e4556ee?w=800&q=80"),
        Event(id = "e2", title = "Bidriware Exhibition", date = "Jan 10, 2026", locationName = "Bidar Fort", description = "Exhibition of fine silver inlay works and live demonstration.", artisanId = "a2", artFormId = "af2", status = "Upcoming", imageUrl = "https://images.unsplash.com/photo-1610486663581-2292f392dd55?w=800&q=80"),
        Event(id = "e3", title = "Channapatna Toy Workshop", date = "Oct 15, 2025", locationName = "Varnam Studio, Channapatna", description = "Learn to paint your own wooden toys.", artisanId = "a3", artFormId = "af3", status = "Past", imageUrl = "https://images.unsplash.com/photo-1596461404969-9ae70f2830c1?w=800&q=80"),
        Event(id = "e4", title = "Dollu Kunitha Festival", date = "Feb 14, 2026", locationName = "Shimoga Ground", description = "A massive gathering of Dollu Kunitha drummers.", artisanId = "a4", artFormId = "af4", status = "Upcoming", imageUrl = "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=800&q=80"),
        Event(id = "e5", title = "Puppet Show: Ramayana", date = "Mar 05, 2026", locationName = "Bellary Heritage Center", description = "A traditional Togalu Gombeyaata shadow puppet show.", artisanId = "a10", artFormId = "af10", status = "Upcoming", imageUrl = "https://images.unsplash.com/photo-1551040333-6c71c48dc9e2?w=800&q=80")
    )

    val mockQuestions = listOf(
        Question(id = "q1", userId = "user1", artFormId = "af1", questionText = "What is the difference between Tenku Thittu and Badagu Thittu?", answerText = "Tenku Thittu has more influence of Carnatic music and circular dance movements, while Badagu Thittu focuses more on facial expressions and high-pitched singing.", guruId = "a1"),
        Question(id = "q2", userId = "user2", artFormId = "af2", questionText = "How do you achieve the black color in Bidriware?", answerText = "The black color comes from a special soil found only in the Bidar fort, which is rich in oxidizing agents like saltpeter.", guruId = "a2")
    )

    val mockReviews = listOf(
        Review(id = "r1", artisanId = "a2", userId = "user3", userName = "Priya M", rating = 5f, comment = "Exquisite craftsmanship! The vase is beautiful and shipping was secure.", isVerifiedBuyer = true, artisanReply = "Thank you Priya! We are glad you appreciate our heritage."),
        Review(id = "r2", artisanId = "a1", userId = "user4", userName = "Sunil K", rating = 5f, comment = "Incredible energy in the performance. A must-watch!", isVerifiedBuyer = false)
    )
}
