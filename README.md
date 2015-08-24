# flickrChallenge
1. Allows the user to browse publicly-viewable images on Flickr in a grid view.
2. If an image is clicked, shows the username of the photographer and any comments that have been made.
  (NOTE: most images don't have comments available)
3. Images are cached locally. (Smaller images for phone and Larger images for tablet)
4. Images' metadata and comments are cached in SQLite


TODO:
1. Add unit tests.
2. Use ContentProvider to provide another level of abstraction.
3. Create a separate layout for tablets.
4. Instead of supporting API 4.0 and above, support API 2.3.3 and above.
5. Allow users to login to access their private photo stream. (Oauth)
6. Create two build flavors called LITE and PRO. Pro should include all of the features, but LITE should not allow users to login.
