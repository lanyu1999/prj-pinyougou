app.controller("searchController", function ($scope, searchService) {
    // 搜索对象
    $scope.searchMap = {"keywords": "", "category": "", "brand": "", "spec": {}};
    // 根据关键字搜索商品
    $scope.search = function () {
        searchService.search($scope.searchMap).success(function (response) {
            $scope.resultMap = response;
        });
    };

// 添加过滤条件
    $scope.addSearchItem = function (key, value) {
        if ("brand" == key || "category" == key || "price" == key) {
            // 如果点击的是品牌或者分类的话
            $scope.searchMap[key] = value;
        } else {
            // 规格
            $scope.searchMap.spec[key] = value;
        }
        // 点击过滤条件后需要重新搜索
        $scope.search();
    };

    //删除过滤条件
    $scope.removeSearchItem = function (key) {
        if ("brand" == key || "category" == key || "price" == key) {
            // 如果点击的是品牌或者分类的话
            $scope.searchMap[key] = "";
        } else {
            // 规格
           delete $scope.searchMap.spec[key];
        }
        // 点击过滤条件后需要重新搜索
        $scope.search();
    }

});