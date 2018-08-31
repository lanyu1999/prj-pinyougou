app.controller("brandController", function ($scope, $controller, brandService) {
//继承 baseController
    $controller("baseController", {$scope: $scope});
    // 查询所有列表数据并绑定到 list对象
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            $scope.list = response;
        });
    };

    // 初始化分页参数
    $scope.paginationConf = {
        currentPage: 1,// 当前页号
        totalItems:
            10,// 总记录数
        itemsPerPage:
            10,// 页大小
        perPageOptions:
            [10, 20, 30, 40, 50],// 可选择的每页大小


        onChange: function () {// 当上述的参数发生变化了后触发
            $scope.reloadList();
        }
    };

// 加载表格数据
    $scope.reloadList = function () {
//$scope.findPage($scope.paginationConf.currentPage,
        $scope.paginationConf.itemsPerPage;
        $scope.search($scope.paginationConf.currentPage,
            $scope.paginationConf.itemsPerPage)
    };

// 分页查询
    $scope.findPage = function (page, rows) {
        brandService.findPage(page, rows).success(function (response) {
            // 更新记录列表
            $scope.list = response.rows;
            // 更新总记录数
            $scope.paginationConf.totalItems = response.total;
        });
    }
    ;

// 保存
    $scope.save = function () {
        var obj;
        if ($scope.entity.id != null) {
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity);
        }
        obj.success(function (response) {
            if (response.success) {
                // 重新加载列表
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });
    }
    ;

// 根据主键查询


    $scope.findOne = function (id) {
        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        });
    };

// 定义一个放置选择了 id的数组
    $scope.selectedIds = [];

    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked) {
            $scope.selectedIds.push(id);
        } else {
            var index = $scope.selectedIds.indexOf(id);
            // 删除位置，删除个数
            $scope.selectedIds.splice(index, 1);
        }
    };

// 批量删除
    $scope.delete = function () {
        if ($scope.selectedIds.length < 1) {
            alert("请选择要删除的记录");
            return;
        }
        if (confirm("确定要删除选中的记录吗？")) {
            brandService.delete($scope.selectedIds).success(function
                (response) {
                if (response.success) {
                    $scope.reloadList();
                    $scope.selectedIds = [];
                } else {
                    alert(response.message);
                }
            });
        }
    };

// 搜索
// 定一个空的搜索条件对象
    $scope.searchEntity = {};
    $scope.search = function (page, rows) {
        brandService.search($scope.searchEntity, page,
            rows).success(function (response) {
            $scope.list = response.rows;
            $scope.paginationConf.totalItems = response.total;
        });
    };

});
