# Jan Loos Import

Tools for importing and creating single VTT modules from Jan Loos patreon uploads.


we have our downloads.csv file with values:
<url>|<name>

e.g url is: https://www.patreon.com/file?h=1&i=2

get the href, download it and it will produce:
```text
    https://www.dropbox.com/s/somepath-redacted/module.json?dl=1
    
    Installation Instructions
    
    1. Open Foundry VTT
    2. Go to Add-On Modules
    3. Click Install Modules
    4. Fill in the following URL in the Manifest URL box: https://www.dropbox.com/s/somepath-redacted/module.json?dl=1
    5. Press Install
    
    6. Open your game world
    7. Go to settings
    8. Click Manage Modules
    9. Click the box next to Name Of Module Here
    10. Save Module Settings
    
    Now the files should be available in your compendium.
```

Extracting the url, and downloading it gives us a VTT module.json file of the format:

```json
{
    "name": "janloos-module-name-X",
    "title": "Jan Loos - Module Name Here",
    "description": "<p>Module Descriptions</p>",
    "version": "1.0.1",
    "minimumCoreVersion": "0.5.0",
    "compatibleCoreVersion": "1.0.0",
    "author": "Jan Loos",
    "packs": [{
        "name": "janloos-module-name-X",
        "label": "Jan Loos - Module Name Here",
        "path": "/Packs/tokens.db",
        "entity": "Actor"
    }],
    "url": "https://www.patreon.com/onlinetabletop/",
    "manifest": "https://www.dropbox.com/s/some-redacted-path/module.json?dl=1",
    "download": "https://www.dropbox.com/s/another-redacted-path/JanLoos-ModuleX.zip?dl=1"
}
```

We wish to combine all the individual packs into a single pack file with different names, labels and paths.
